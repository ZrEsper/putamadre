"""Expression/pose regression simulation, not an EMF or Minecraft runtime test."""
import json,zipfile,re,math,random
from pathlib import Path
TOKEN=re.compile(r'\s*(?:(\d+(?:\.\d*)?|\.\d+)|([A-Za-z_][\w.]*)|(\|\||&&|==|!=|<=|>=|[()+*/!,<>\-]))')
PREC={'||':1,'&&':2,'==':3,'!=':3,'<':4,'>':4,'<=':4,'>=':4,'+':5,'-':5,'*':6,'/':6}
class Parser:
 def __init__(self,s):
  self.tokens=[];at=0
  while at<len(s):
   m=TOKEN.match(s,at)
   if not m:raise AssertionError('unsupported syntax '+s[at:])
   self.tokens.append(('n',float(m[1])) if m[1] else ('id',m[2]) if m[2] else ('op',m[3]));at=m.end()
  self.i=0
 def pop(self):t=self.tokens[self.i];self.i+=1;return t
 def peek(self,v):return self.i<len(self.tokens) and self.tokens[self.i][1]==v
 def expression(self,minimum=0):
  kind,value=self.pop()
  if value in ['-','!','+']:lhs=('unary',value,self.expression(7))
  elif value=='(':
   lhs=self.expression();assert self.pop()[1]==')'
  elif kind=='n':lhs=('n',value)
  elif kind=='id':
   if self.peek('('):
    self.pop();args=[]
    if not self.peek(')'):
     while True:
      args.append(self.expression())
      if not self.peek(','):break
      self.pop()
    assert self.pop()[1]==')';lhs=('fn',value,args)
   else:lhs=('id',value)
  else:raise AssertionError(value)
  while self.i<len(self.tokens) and self.tokens[self.i][1] in PREC and PREC[self.tokens[self.i][1]]>=minimum:
   operator=self.pop()[1];lhs=('binary',operator,lhs,self.expression(PREC[operator]+1))
  return lhs
 def parse(self):x=self.expression();assert self.i==len(self.tokens);return x
FUNCTIONS={'sin':math.sin,'cos':math.cos,'abs':abs,'floor':math.floor,'fmod':math.fmod,'sqrt':math.sqrt,'pow':pow,'min':min,'clamp':lambda v,a,b:max(a,min(b,v)),'between':lambda v,a,b:a<=v<=b,'torad':math.radians,'random':lambda x:random.Random(x).random()}
def evaluate(t,values):
 kind=t[0]
 if kind=='n':return t[1]
 if kind=='id':
  if t[1] in ['pi']:return math.pi
  if t[1] in values:return values[t[1]]
  if t[1].startswith(('var.','varb.')):return 0
  raise AssertionError('unknown input '+t[1])
 if kind=='unary':v=evaluate(t[2],values);return -v if t[1]=='-' else v if t[1]=='+' else not v
 if kind=='fn':
  name,args=t[1:]
  if name=='if':
   for i in range(0,len(args)-1,2):
    if evaluate(args[i],values):return evaluate(args[i+1],values)
   return evaluate(args[-1],values)
  return FUNCTIONS[name](*[evaluate(x,values) for x in args])
 op=t[1];a=evaluate(t[2],values)
 if op=='&&':return a and evaluate(t[3],values)
 if op=='||':return a or evaluate(t[3],values)
 b=evaluate(t[3],values)
 if op=='+':return a+b
 if op=='-':return a-b
 if op=='*':return a*b
 if op=='/':return a/b
 if op=='==':return a==b
 if op=='!=':return a!=b
 if op=='<':return a<b
 if op=='>':return a>b
 if op=='<=':return a<=b
 if op=='>=':return a>=b
 raise AssertionError(op)
root=Path(__file__).resolve().parent;z=zipfile.ZipFile(root/'TZP-1.21.1-Gore.5-progressive-trauma.zip');frames=0;expressions=0
for kind in ['zombie','husk','drowned','drowned_outer']:
 model=json.loads(z.read('assets/minecraft/optifine/cem/'+kind+'.jem'));lines=[(key,Parser(expr).parse()) for a in model['models'][0]['animations'] for key,expr in a.items()];expressions+=len(lines)
 material_ids=[]
 def collect(n):
  if n.get('texture','').startswith('zg_'):material_ids.append(n['id'])
  for c in n.get('submodels',[]):collect(c)
 for m in model['models']:collect(m)
 for scenario in ['healthy','burning','injured','fractured','far']:
  values={key:0 for key,_ in lines}
  observed=[]
  for frame in range(150):
   values.update({'age':400+frame/3,'frame_counter':frame+1,'frame_time':1/60,'id':2081,'distance':64 if scenario=='far' else 8,'is_burning':scenario=='burning','is_aggressive':True,'is_child':False,'is_riding':False,'is_in_water':False,'is_in_lava':False,'is_on_ground':True,'limb_swing':frame*.14,'limb_speed':.6,'head_yaw':math.sin(frame*.03)*18,'head_pitch':5,'hurt_time':max(0,10-frame) if scenario=='injured' else 0,'is_hurt':scenario=='injured' and frame<10,'swing_progress':0,'pos_x':0,'pos_y':0,'pos_z':0,'player_pos_x':5,'player_pos_y':0,'player_pos_z':0,'zg_broken':15 if scenario=='fractured' else 0,'zg_loss':1|4|16|64 if scenario=='injured' else 0,'zg_wounds':255 if scenario=='injured' else 0,'zg_exit':128 if scenario=='injured' else 0})
   for key,tree in lines:
    value=evaluate(tree,values);assert math.isfinite(float(value)),(kind,scenario,key,value);values[key]=value
   if scenario in ['healthy','burning','far']:
    for ident in material_ids:assert not values[ident+'.visible'] and values[ident+'.sx']==0,(scenario,ident)
   if scenario=='far':assert values['var.zg_detail']==0
   for key in ['left_arm.rx','right_arm.rx','body.rx','body.rz','headwear.rz']:assert abs(values[key])<math.pi*2,(kind,key,values[key])
   observed.append(values['left_arm.rx']);frames+=1
  assert max(observed)-min(observed)>(.01 if scenario=='fractured' else .05),(kind,scenario,'stiff arm pose')
(root/'animation-validation.json').write_text(json.dumps({'parsed_animation_lines':expressions,'simulated_frames':frames,'healthy_and_burning_uninjured_materials_hidden':True,'bounded_finite_poses':True,'game_runtime_tested':False},indent=2)+'\n')
print('Animation lines parsed:',expressions,'Healthy, fire, injured and distant pose frames checked:',frames)
