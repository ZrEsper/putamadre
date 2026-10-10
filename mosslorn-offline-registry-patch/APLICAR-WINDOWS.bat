@echo off
setlocal
if exist "%LocalAppData%\Python\pythoncore-3.14-64\python.exe" (
 "%LocalAppData%\Python\pythoncore-3.14-64\python.exe" "%~dp0aplicar.py"
) else (
 py -3 "%~dp0aplicar.py"
)
pause
