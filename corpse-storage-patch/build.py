from pathlib import Path
import copy, hashlib, json, os, shutil, subprocess, urllib.request, zipfile

root = Path(__file__).resolve().parent
build = root / 'build'
tools = build / 'tools'
tools.mkdir(parents=True, exist_ok=True)
for name, digest in json.loads((root / 'tools.sha256.json').read_text()).items():
    path = tools / name
    if not path.exists() or hashlib.sha256(path.read_bytes()).hexdigest() != digest:
        artifact = 'org/eclipse/jdt/ecj/3.39.0/ecj-3.39.0.jar' if name == 'ecj.jar' else f'org/ow2/asm/{name[:-4]}/9.7.1/{name[:-4]}-9.7.1.jar'
        data = urllib.request.urlopen('https://repo.maven.apache.org/maven2/' + artifact, timeout=60).read()
        assert hashlib.sha256(data).hexdigest() == digest, name
        path.write_bytes(data)
    assert hashlib.sha256(path.read_bytes()).hexdigest() == digest, name

def run(*args):
    subprocess.run(list(map(str, args)), cwd=root, check=True)

ecj = ['java', '-jar', tools / 'ecj.jar', '-21', '-proc:none', '-warn:none']
asm = os.pathsep.join(str(p) for p in tools.glob('asm*.jar'))
run(*ecj, '-cp', asm, '-d', build / 'patch-tool', root / 'Patch.java', root / 'Verify.java')
inputs = json.loads((root / 'inputs.sha256.json').read_text())
names = {
    'quietly': 'quietly-1_0_0-cooldown-v14-corpse-outline-no-spill.jar',
    'remains': 'zombie-remains-loot-1.6.1-neoforge-1.21.1-military-nvg-mythic.7-npc-full-cargo.jar',
    'npc': 'tacznpcs-2.1.0-1.21.1-tactical.14-corpse-cargo.jar',
    'iso': 'dungeons_iso-neoforge-3.6.2+1.21.1-zomboid-tacz.27-corpse-outline.jar',
}
patched = []
for kind, spec in inputs.items():
    base = root / 'base' / spec['name']
    assert hashlib.sha256(base.read_bytes()).hexdigest() == spec['sha256'], base.name
    path = build / f'{kind}.jar'
    run('java', '-cp', str(build / 'patch-tool') + os.pathsep + asm, 'Patch', kind, base, path)
    patched.append(path)

classes = build / 'classes'
if classes.exists():
    shutil.rmtree(classes)
run(*ecj, '-cp', os.pathsep.join(map(str, patched)), '-d', classes,
    *sorted((root / 'src').rglob('*.java')), *sorted((root / 'test-src').rglob('*.java')))

prefixes = {
    'quietly': ('dev/zomboid/storage/', 'dev/zomboid/scavenging/KeepLoot', 'cn/kafei/mixin/'),
    'remains': ('dev/zomboid/remains/delivery/',),
    'npc': ('mod/cdv/tnpc/tactical/CorpseDelivery',),
    'iso': ('dev/zomboid/isotacz/BodyCursor',),
}
report = {}
production = {p.relative_to(root / 'src').as_posix()[:-5] for p in (root / 'src').rglob('*.java')}
(root / 'output').mkdir(exist_ok=True)
for kind, name in names.items():
    add = {p.relative_to(classes).as_posix(): p.read_bytes() for p in classes.rglob('*.class')
           if p.relative_to(classes).as_posix().startswith(prefixes[kind]) and p.relative_to(classes).as_posix()[:-6].split('$')[0] in production}
    if kind == 'quietly':
        with zipfile.ZipFile(build / 'quietly.jar') as z:
            config = json.loads(z.read('quietly.mixins.json'))
            config['mixins'] += ['ExpandedChestMenuMixin', 'ExpandedChestDropsMixin']
            add['quietly.mixins.json'] = (json.dumps(config, indent=2) + '\n').encode()
    output = root / 'output' / name
    with zipfile.ZipFile(build / f'{kind}.jar') as z, zipfile.ZipFile(output, 'w', zipfile.ZIP_DEFLATED) as w:
        for entry in z.infolist():
            w.writestr(copy.copy(entry), add.pop(entry.filename, z.read(entry)))
        for entry, data in sorted(add.items()):
            w.writestr(entry, data)
    base = root / 'base' / inputs[kind]['name']
    with zipfile.ZipFile(base) as a, zipfile.ZipFile(output) as b:
        assert b.testzip() is None
        changed = [n for n in a.namelist() if a.read(n) != b.read(n)]
        expected = {
            'quietly': {'cn/kafei/interact/Interaction.class', 'dev/zomboid/scavenging/Loot.class', 'cn/kafei/mixin/InteractionOutlineMixin.class', 'quietly.mixins.json'},
            'remains': {'dev/zomboid/remains/ZombieRemainsLoot.class', 'dev/zomboid/remains/ZombieRemainsLoot$CorpseData.class'},
            'npc': {'mod/cdv/tnpc/tactical/SurvivorPatch.class'},
            'iso': {'dev/zomboid/isotacz/Bridge.class'},
        }[kind]
        assert set(changed) == expected, (kind, changed)
        added = set(b.namelist()) - set(a.namelist())
        assert all(n.endswith('.class') and n.startswith(prefixes[kind]) for n in added), added
        report[kind] = {'input_sha256': inputs[kind]['sha256'], 'output_sha256': hashlib.sha256(output.read_bytes()).hexdigest(), 'changed_entries': changed, 'added_entries': sorted(added)}
    run('java', '-cp', str(build / 'patch-tool') + os.pathsep + asm, 'Verify', output)

classpath = os.pathsep.join([str(classes)] + [str(root / 'output' / n) for n in names.values()])
for main in ['dev.zomboid.scavenging.StorageChecks', 'dev.zomboid.remains.delivery.CargoChecks', 'dev.zomboid.isotacz.BodyChecks', 'NativeChecks']:
    run('java', '-Xverify:all', '-cp', classpath, main)
run(*ecj, '-cp', classpath, '-d', build / 'cargo-tests', *sorted((root / 'cargo-doubles').rglob('*.java')))
run('java', '-Xverify:all', '-cp', str(build / 'cargo-tests') + os.pathsep + classpath, 'dev.zomboid.remains.delivery.DeathChecks')
run(*ecj, '-cp', classpath, '-d', build / 'body-tests', *sorted((root / 'body-doubles').rglob('*.java')))
run('java', '-Xverify:all', '-cp', str(build / 'body-tests') + os.pathsep + classpath, 'dev.zomboid.isotacz.CursorChecks')
report['checks'] = {'quietly_storage': 54, 'npc_cargo': 17, 'corpse_outline': 13, 'native_corpse_and_pending_save_load': 13, 'npc_death_delivery': 27, 'isometric_body_cursor': 14}
report['minecraft_runtime_tested'] = False
(root / 'validation.json').write_text(json.dumps(report, indent=2) + '\n')
for name in names.values():
    shutil.copy2(root / 'output' / name, root.parent / 'mods' / name)
print('All checks passed; four replacement JARs copied to mods/.')
