#!/usr/bin/env python3
import shutil
import os
import re

src = '/Users/peizheng/StudioProjects/meta-dat-android/samples/CameraAccess'
dst = '/Users/peizheng/StudioProjects/meta-dat-android/samples/SmartPicker'

# Copy directory structure, excluding build artifacts
if os.path.exists(dst):
    shutil.rmtree(dst)

def ignore_patterns(dir, files):
    ignored = []
    for f in files:
        if f in ['build', '.gradle'] or f.endswith('.iml'):
            ignored.append(f)
    return ignored

shutil.copytree(src, dst, ignore=ignore_patterns)
print('Directory copied successfully')
