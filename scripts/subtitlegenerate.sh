asset="$1"
assetid="$2"
cwd="$3"
srt="$4"
script="/usr/local/bin/ffmpeg"
$script -i $asset $cwd/public/$assetid/$srt
