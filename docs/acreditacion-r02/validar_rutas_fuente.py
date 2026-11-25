import json,re
from pathlib import Path
p=Path(__file__).resolve().parents[2] / 'src/main/java/com/teletrabajo/controller/acreditacion'
found=set()
for f in p.glob('*.java'):
 txt=f.read_text()
 root=re.search(r'@RequestMapping\("([^\"]+)"\)',txt)
 if not root:continue
 for annotation in re.finditer(r'@(Get|Post|Put|Delete|Patch)Mapping\b(?:\(([^)]*)\))?',txt):
  verb=annotation.group(1).upper();attrs=annotation.group(2)
  if attrs is None:routes=['']
  elif attrs.startswith('consumes='):routes=['']
  else:routes=re.findall(r'"(\/[^\"]*)"',attrs)
  for path in routes:found.add((verb,root.group(1)+path))
spec=json.load(open(Path(__file__).with_name('ACR-OPENAPI-OBJETIVO-R01.json')))
want={(verb.upper(),path) for path,ops in spec['paths'].items() for verb in ops if verb in ['get','post','put','delete','patch']}
missing=want-found
print('contrato',len(want),'mapeadas',len(want&found),'faltantes',len(missing))
for x in sorted(missing):print(x)
