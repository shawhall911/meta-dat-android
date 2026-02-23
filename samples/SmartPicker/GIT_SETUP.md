# Git Setup Guide for SmartView

## Quick Commands to Push to GitHub

### 1. Check Current Status
```bash
cd /Users/peizheng/StudioProjects/meta-dat-android
git status
```

### 2. Add SmartView Files
```bash
# Add all SmartView files
git add samples/SmartView/

# Or add specific files
git add samples/SmartView/app/
git add samples/SmartView/gradle/
git add samples/SmartView/*.gradle.kts
git add samples/SmartView/*.properties
git add samples/SmartView/README.md
git add samples/SmartView/TESTING_GUIDE.md
```

### 3. Commit Changes
```bash
git commit -m "Add SmartView app with AI-powered scene understanding

- Camera streaming from Meta AI glasses
- Cloud AI integration for scene analysis
- Text-to-speech audio feedback
- Real-time AI analysis overlay
- Complete UI implementation
- Testing guide included"
```

### 4. Push to GitHub

**If you already have a remote:**
```bash
git push origin main
# or
git push origin master
```

**If you need to add a remote:**
```bash
# Replace with your GitHub username and repo name
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO_NAME.git
git branch -M main
git push -u origin main
```

## Files to Exclude (if needed)

Make sure these are in `.gitignore`:
- `local.properties` (contains sensitive tokens)
- `*.keystore` (if you want to keep it private)
- `build/` directories
- `.gradle/` directories

## Recommended .gitignore Entries

Add to `.gitignore` if not already there:
```
# SmartView specific
samples/SmartView/local.properties
samples/SmartView/app/sample.keystore
samples/SmartView/app/build/
samples/SmartView/build/
samples/SmartView/.gradle/
```

## Alternative: Create Separate Repository

If you want SmartView as a separate repository:

```bash
cd samples/SmartView

# Initialize new git repo
git init
git add .
git commit -m "Initial commit: SmartView app"

# Create repo on GitHub, then:
git remote add origin https://github.com/YOUR_USERNAME/smartview.git
git branch -M main
git push -u origin main
```

## Important Notes

1. **Don't commit `local.properties`** - It contains your GitHub token
2. **Consider excluding `sample.keystore`** - Or use a different keystore for production
3. **Include `TESTING_GUIDE.md`** - Helpful for others
4. **Include `README.md`** - Project documentation
