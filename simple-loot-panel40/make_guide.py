"""Render the offline plain-language guide with no external resources or dependencies."""
from pathlib import Path
import html,re

def inline(text):
 text=html.escape(text);text=re.sub(r'\*\*(.*?)\*\*',r'<strong>\1</strong>',text);return re.sub(r'`(.*?)`',r'<code>\1</code>',text)
lines=Path('GUIA.md').read_text().splitlines();out=[];i=0
while i<len(lines):
 line=lines[i]
 if not line.strip():i+=1;continue
 if line.startswith('|'):
  rows=[]
  while i<len(lines) and lines[i].startswith('|'):
   parts=[s.strip() for s in lines[i].strip('|').split('|')]
   if not all(re.fullmatch(r':?-+:?',s) for s in parts):rows.append(parts)
   i+=1
  out.append('<div class="table"><table>'+''.join('<tr>'+''.join(('<th>' if r==0 else '<td>')+inline(c)+('</th>' if r==0 else '</td>') for c in row)+'</tr>' for r,row in enumerate(rows))+'</table></div>');continue
 if line.startswith('#'):
  level=len(line)-len(line.lstrip('#'));out.append(f'<h{level}>'+inline(line[level:].strip())+f'</h{level}>')
 else:out.append('<p>'+inline(line)+'</p>')
 i+=1
Path('GUIA.html').write_text('''<!doctype html><html lang="es"><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Zombies Poldo · Panel sencillo</title><style>body{margin:0;background:#121414;color:#e4e4dc;font:17px/1.65 system-ui,sans-serif}main{max-width:1000px;margin:auto;padding:32px 22px 70px}h1{color:#f2d4cb;border-bottom:3px solid #8a3631;padding-bottom:20px}h2{margin-top:48px;color:#d8b38e}h3{color:#d9c7ad}p{max-width:900px}strong{color:#fff1d9}.table{overflow-x:auto}table{width:100%;border-collapse:collapse;background:#1b2020}th,td{text-align:left;border:1px solid #394141;padding:12px;vertical-align:top}th{background:#3d2927;color:#f8e6d4}td:first-child{min-width:160px;color:#e8ccaf}code{background:#303535;padding:2px 5px;border-radius:4px}footer{margin-top:50px;color:#adb5aa;font-size:14px}</style><main>'''+''.join(out)+'<footer>ZOMBIES POLDO · Guía sin conexión · Hordes .40 · Panel sencillo · Loot nuevo</footer></main></html>')
