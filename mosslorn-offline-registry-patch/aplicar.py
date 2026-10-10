"""Apply the prepared Mosslorn update to a NEW COPY. Python 3, standard library only."""
from pathlib import Path
import sys,json,hashlib,zipfile,shutil,struct
from pack_selection import update_level
ROOT=Path(__file__).resolve().parent

def sha(path):
 h=hashlib.sha256()
 with path.open('rb') as f:
  for chunk in iter(lambda:f.read(1024*1024),b''):h.update(chunk)
 return h.hexdigest()
def apply(world,dest=None):
 world=Path(world).resolve()
 if not (world/'level.dat').is_file():raise ValueError('Elige la carpeta del mundo que contiene level.dat.')
 manifest=json.loads((ROOT/'manifest.json').read_text(encoding='utf-8'))
 for rel,expected in manifest['source_hashes'].items():
  path=world/rel
  if not path.is_file() or sha(path)!=expected:raise ValueError('El mundo cambió desde que lo enviaste, o es otra copia: '+rel+'. No se modificó el original.')
 dest=Path(dest).resolve() if dest else world.with_name(world.name+'-v7-registrado')
 if dest==world or world in dest.parents or dest in world.parents or dest.exists():raise ValueError('La carpeta de salida debe ser nueva y estar separada del original: '+str(dest))
 shutil.copytree(world,dest)
 try:
  with zipfile.ZipFile(ROOT/'cambios.zip') as z:
   if z.testzip() is not None:raise ValueError('El paquete de cambios está dañado.')
   for rel,patch in manifest['regions'].items():
    old=(world/rel).read_bytes();header=z.read(patch['header']);body=bytearray()
    for i in range(1024):
     offset=int.from_bytes(old[i*4:i*4+4],'big')>>8
     if not offset:continue
     length=int.from_bytes(old[offset*4096:offset*4096+4],'big')
     record=z.read(patch['records'][str(i)]) if str(i) in patch['records'] else old[offset*4096:offset*4096+4+length]
     body.extend(record);body.extend(bytes((-len(record))%4096))
    (dest/rel).write_bytes(header+body)
   for rel,entry in manifest['files'].items():
    target=dest/rel;target.parent.mkdir(parents=True,exist_ok=True);target.write_bytes(z.read(entry))
  (dest/'level.dat').write_bytes(update_level((world/'level.dat').read_bytes(),manifest['remove_packs'],manifest['add_pack']))
  for rel in manifest['remove']:
   path=dest/rel
   if path.is_dir():shutil.rmtree(path)
   elif path.exists():path.unlink()
  for rel,expected in manifest['output_hashes'].items():
   if sha(dest/rel)!=expected:raise ValueError('No coincide la verificación final: '+rel)
  (dest/'Mosslorn-v7-LEEME.txt').write_text('Todos los cofres y barriles vanilla existentes están registrados, incluidos los renombrados. Objetos y nombres conservados. El loot pendiente se genera al abrir o al escanear chunks cargados. No hace falta recorrer el mapa para crear el registro. Datapack v7 instalado y habilitado. No ejecutado en Minecraft durante la preparación.\n',encoding='utf-8')
 except Exception:
  print('La copia de salida quedó incompleta: no la abras. El mundo original permanece intacto.',flush=True);raise
 return dest
if __name__=='__main__':
 gui=None
 try:
  if len(sys.argv)>1:world=sys.argv[1]
  else:
   import tkinter as tk
   from tkinter import filedialog,messagebox
   gui=tk.Tk();gui.withdraw()
   world=filedialog.askdirectory(title='Minecraft cerrado: elige el mundo que enviaste (contiene level.dat)')
   if not world:sys.exit(0)
  print('Comprobando y creando una copia nueva. Mantén Minecraft cerrado...',flush=True)
  dest=apply(world)
  text='Listo. Abre la NUEVA copia: '+str(dest)+'\nTu mundo original no se modificó.'
  print(text,flush=True)
  if gui is not None:messagebox.showinfo('Mosslorn v7',text)
 except Exception as e:
  print('ERROR:',e,flush=True)
  if gui is not None:messagebox.showerror('Mosslorn v7',str(e))
  sys.exit(1)
