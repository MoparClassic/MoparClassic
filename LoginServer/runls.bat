taskkill /F /IM java.exe
title LoginServer
set PATH=%PATH%;ant\bin
ant runls
pause