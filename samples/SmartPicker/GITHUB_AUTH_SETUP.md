# GitHub Authentication Setup

## Option 1: Use Personal Access Token (PAT) - Recommended

### Step 1: Create a Personal Access Token on GitHub

1. Go to GitHub.com → Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Click "Generate new token (classic)"
3. Give it a name like "SmartView Git Access"
4. Select scopes:
   - ✅ `repo` (full control of private repositories)
   - ✅ `workflow` (if using GitHub Actions)
5. Click "Generate token"
6. **Copy the token immediately** (you won't see it again!)

### Step 2: Use Token When Pushing

When you push, use the token as your password:

```bash
git push origin main
# Username: your-github-username
# Password: paste-your-token-here (not your GitHub password!)
```

### Step 3: Store Credentials (Optional but Recommended)

**macOS Keychain:**
```bash
# This will store your credentials in macOS Keychain
git config --global credential.helper osxkeychain

# Then push (it will ask once, then remember)
git push origin main
```

**Or use credential helper:**
```bash
git config --global credential.helper store
# Next push will ask for username/token, then save it
```

## Option 2: Use SSH Keys (More Secure)

### Step 1: Check for Existing SSH Key

```bash
ls -al ~/.ssh
```

### Step 2: Generate SSH Key (if needed)

```bash
ssh-keygen -t ed25519 -C "your_email@example.com"
# Press Enter to accept default location
# Enter a passphrase (optional but recommended)
```

### Step 3: Add SSH Key to GitHub

```bash
# Copy your public key
cat ~/.ssh/id_ed25519.pub
# Copy the entire output
```

1. Go to GitHub.com → Settings → SSH and GPG keys
2. Click "New SSH key"
3. Paste your public key
4. Click "Add SSH key"

### Step 4: Change Remote to SSH

```bash
cd /Users/peizheng/StudioProjects/meta-dat-android

# Check current remote
git remote -v

# Change to SSH (replace with your username/repo)
git remote set-url origin git@github.com:YOUR_USERNAME/YOUR_REPO.git

# Test connection
ssh -T git@github.com

# Now push
git push origin main
```

## Option 3: Use GitHub CLI (gh)

```bash
# Install GitHub CLI (if not installed)
brew install gh

# Authenticate
gh auth login

# Follow the prompts to authenticate
# Then push normally
git push origin main
```

## Quick Fix for Current Push

If you just want to push now with a token:

```bash
# Get your token from GitHub (see Option 1, Step 1)
# Then push with token in URL (one-time)
git push https://YOUR_TOKEN@github.com/YOUR_USERNAME/YOUR_REPO.git main

# Or use credential helper for future
git config --global credential.helper osxkeychain
git push origin main
# Enter username and token when prompted
```
