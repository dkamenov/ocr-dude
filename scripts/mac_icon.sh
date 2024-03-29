#!/bin/sh

set -xe

input_file=$1
iconset_name=$2

[ ! -d $iconset_name ] && mkdir -p $iconset_name

sips -z 16 16     $input_file --out "${iconset_name}/icon_16x16.png";
sips -z 32 32     $input_file --out "${iconset_name}/icon_16x16@2x.png";
sips -z 32 32     $input_file --out "${iconset_name}/icon_32x32.png";
sips -z 64 64     $input_file --out "${iconset_name}/icon_32x32@2x.png";
sips -z 128 128   $input_file --out "${iconset_name}/icon_128x128.png";
sips -z 256 256   $input_file --out "${iconset_name}/icon_128x128@2x.png";
sips -z 256 256   $input_file --out "${iconset_name}/icon_256x256.png";
sips -z 512 512   $input_file --out "${iconset_name}/icon_256x256@2x.png";
sips -z 512 512   $input_file --out "${iconset_name}/icon_512x512.png";

iconutil -c icns $iconset_name