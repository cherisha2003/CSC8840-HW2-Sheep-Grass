#!/usr/bin/env bash
set -e
sudo apt-get update
sudo DEBIAN_FRONTEND=noninteractive apt-get install -y xvfb x11vnc novnc websockify
Xvfb :99 -screen 0 1280x900x24 >/tmp/sheep-xvfb.log 2>&1 &
export DISPLAY=:99
x11vnc -display :99 -forever -shared -nopw -listen localhost -rfbport 5900 >/tmp/sheep-vnc.log 2>&1 &
websockify --web /usr/share/novnc/ 6080 localhost:5900 >/tmp/sheep-websockify.log 2>&1 &
echo 'Display ready. In Codespaces, open forwarded port 6080 and select vnc.html.'
echo 'In this terminal, run: DISPLAY=:99 ./run.sh 5 3000'
