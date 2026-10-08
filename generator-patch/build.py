#!/usr/bin/env python3
"""Reproduce the binary patch and run isolated checks; requires Python 3 and Java 21."""
from pathlib import Path
import hashlib
import json
import os
import subprocess
import sys
import tempfile
import urllib.request
import zipfile

HERE = Path(__file__).resolve().parent
REPO = HERE.parent
BASE = REPO / 'mods/zomboid-survival-1.21.1-1.0.0-firstaid-ui-restored.jar'
OUTPUT = Path(sys.argv[1]).resolve() if len(sys.argv) > 1 else REPO / 'mods/zomboid-survival-1.21.1-1.0.2-generator-ui.jar'
CACHE = Path(os.environ.get('GENERATOR_BUILD_CACHE', '/tmp/zomboid-generator-build-tools'))
ARTIFACTS = {
    'ecj.jar': ('org/eclipse/jdt/ecj/3.39.0/ecj-3.39.0.jar', '01f5a92ac19bb2b3bf85e295a68f2c73c264369109158b566ce9b490af982948'),
    'asm.jar': ('org/ow2/asm/asm/9.7.1/asm-9.7.1.jar', '8cadd43ac5eb6d09de05faecca38b917a040bb9139c7edeb4cc81c740b713281'),
    'asm-tree.jar': ('org/ow2/asm/asm-tree/9.7.1/asm-tree-9.7.1.jar', '9929881f59eb6b840e86d54570c77b59ce721d104e6dfd7a40978991c2d3b41f'),
}

def run(*args):
    subprocess.run([str(a) for a in args], check=True)

CACHE.mkdir(parents=True, exist_ok=True)
for name, (remote, expected) in ARTIFACTS.items():
    target = CACHE / name
    if not target.exists():
        with urllib.request.urlopen('https://repo.maven.apache.org/maven2/' + remote) as response:
            data = response.read()
        if hashlib.sha256(data).hexdigest() != expected:
            raise RuntimeError('Downloaded artifact checksum mismatch: ' + name)
        target.write_bytes(data)
    if hashlib.sha256(target.read_bytes()).hexdigest() != expected:
        raise RuntimeError('Cached artifact checksum mismatch: ' + name)

with tempfile.TemporaryDirectory(prefix='zomboid-generator-') as directory:
    build = Path(directory)
    stubs, classes, tools, runtime, tests = [build / name for name in ('api-doubles', 'classes', 'tools', 'runtime', 'tests')]
    run(sys.executable, '-B', HERE / 'tests/make_api_doubles.py', stubs)
    compiler = ['java', '-jar', CACHE / 'ecj.jar', '-21', '-proc:none', '-warn:none']
    sources = sorted(stubs.rglob('*.java')) + sorted((HERE / 'src').rglob('*.java'))
    run(*compiler, '-d', classes, *sources)
    asm_path = os.pathsep.join(str(CACHE / name) for name in ('asm.jar', 'asm-tree.jar'))
    run(*compiler, '-cp', asm_path, '-d', tools, HERE / 'PatchGenerator.java', HERE / 'tests/MixinAnnotationChecks.java')
    temporary_jar = build / 'output.jar'
    run('java', '-cp', str(tools) + os.pathsep + asm_path, 'PatchGenerator', BASE, classes, temporary_jar)
    # Add production sound assets, subtitles and food merge mixins without touching other entries.
    enriched = build / 'complete.jar'
    with zipfile.ZipFile(temporary_jar) as raw, zipfile.ZipFile(enriched, 'w', zipfile.ZIP_DEFLATED) as complete:
        for entry in raw.infolist():
            data = raw.read(entry.filename)
            if entry.filename == 'zomboid_survival.mixins.json':
                config = json.loads(data)
                config['mixins'].extend(['FoodMenuMergeMixin', 'FoodSlotMergeMixin', 'FoodInventoryMergeMixin', 'FoodDroppedMergeMixin'])
                data = json.dumps(config, ensure_ascii=False, indent=2).encode('utf-8')
            if entry.filename in ('assets/zomboid_survival/lang/es_es.json', 'assets/zomboid_survival/lang/en_us.json'):
                lang = json.loads(data)
                lang['subtitles.zomboid_survival.generator_running'] = 'Generador en marcha' if 'es_es' in entry.filename else 'Generator running'
                data = json.dumps(lang, ensure_ascii=False, indent=2).encode('utf-8')
            complete.writestr(entry, data)
        for file in sorted((HERE / 'resources').rglob('*')):
            if file.is_file():
                entry = zipfile.ZipInfo(file.relative_to(HERE / 'resources').as_posix(), (1980,1,1,0,0,0))
                entry.compress_type = zipfile.ZIP_DEFLATED
                complete.writestr(entry, file.read_bytes())
    temporary_jar = enriched
    with zipfile.ZipFile(BASE) as original, zipfile.ZipFile(temporary_jar) as patched:
        assert patched.testzip() is None
        changed = {n for n in original.namelist() if original.read(n) != patched.read(n)}
        assert changed == {'dev/zomboid/survival/MachineEntity.class', 'dev/zomboid/survival/Client.class', 'META-INF/neoforge.mods.toml', 'zomboid_survival.mixins.json', 'assets/zomboid_survival/lang/es_es.json', 'assets/zomboid_survival/lang/en_us.json'}, changed
        added = set(patched.namelist()) - set(original.namelist())
        new_classes = {n for n in added if n.endswith('.class')}
        assert len(new_classes) == 13 and all(n.startswith(('dev/zomboid/survival/Generator', 'dev/zomboid/survival/FoodStacking', 'dev/zomboid/survival/mixin/Food')) for n in new_classes), new_classes
        assert added - new_classes == {'assets/zomboid_survival/sounds.json', 'assets/zomboid_survival/sounds/generator_running.ogg'}, added
        config = json.loads(patched.read('zomboid_survival.mixins.json'))
        for mixin in config['mixins']:
            assert config['package'].replace('.', '/') + '/' + mixin + '.class' in patched.namelist()
        for name in patched.namelist():
            if name.startswith(('dev/zomboid/survival/MachineEntity', 'dev/zomboid/survival/Generator', 'dev/zomboid/survival/FoodStacking', 'dev/zomboid/survival/Freshness')):
                file = runtime / name
                file.parent.mkdir(parents=True, exist_ok=True)
                file.write_bytes(patched.read(name))
    run('java', '-cp', str(tools) + os.pathsep + asm_path, 'MixinAnnotationChecks', temporary_jar)
    test_classpath = str(runtime) + os.pathsep + str(classes)
    run(*compiler, '-cp', test_classpath, '-d', tests, HERE / 'tests/GeneratorChecks.java', HERE / 'tests/EffectsAndFoodChecks.java')
    run('java', '-Xverify:all', '-cp', str(tests) + os.pathsep + test_classpath, 'GeneratorChecks')
    run('java', '-Xverify:all', '-cp', str(tests) + os.pathsep + test_classpath, 'EffectsAndFoodChecks')
    OUTPUT.parent.mkdir(parents=True, exist_ok=True)
    OUTPUT.write_bytes(temporary_jar.read_bytes())
print('Output:', OUTPUT)
print('SHA256:', hashlib.sha256(OUTPUT.read_bytes()).hexdigest())
print('Checks use signature-based Minecraft/NeoForge API doubles; in-game integration is not verified.')
