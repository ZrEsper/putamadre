@echo off
setlocal
set "MOSS_PY=%LocalAppData%\Python\pythoncore-3.14-64\python.exe"
if not exist "%MOSS_PY%" set "MOSS_PY=python"
"%MOSS_PY%" -c "import nbtlib" >nul 2>&1
if errorlevel 1 (
 "%MOSS_PY%" -m pip install nbtlib==2.0.4
 if errorlevel 1 goto fin
)
"%MOSS_PY%" "%~dp0convertir.py"
:fin
pause
