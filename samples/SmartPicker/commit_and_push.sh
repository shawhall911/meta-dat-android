#!/bin/bash

# Script to commit and push SmartPicker to GitHub

set -e  # Exit on error

cd /Users/peizheng/StudioProjects/meta-dat-android

echo "📋 Checking git status..."
git status

echo ""
echo "📦 Adding SmartPicker files..."
git add samples/SmartPicker/

echo ""
echo "📝 Committing changes..."
git commit -m "Add SmartPicker: AI-powered scene understanding app

Features:
- Camera streaming from Meta AI glasses
- Cloud AI integration for scene analysis  
- Text-to-speech audio feedback
- Real-time AI analysis overlay
- Complete UI and testing guide"

echo ""
echo "🔍 Checking remote repository..."
git remote -v

echo ""
echo "⚠️  GitHub requires authentication via Personal Access Token (PAT) or SSH"
echo "   See GITHUB_AUTH_SETUP.md for setup instructions"
echo ""
read -p "Push to GitHub? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]
then
    echo "🚀 Pushing to GitHub..."
    echo "   If prompted for password, use your Personal Access Token (not GitHub password)"
    echo "   Get a token at: https://github.com/settings/tokens"
    echo ""
    git push origin main || git push origin master
    echo "✅ Done! SmartPicker has been pushed to GitHub."
else
    echo "⏸️  Skipped push. Run 'git push origin main' when ready."
fi
