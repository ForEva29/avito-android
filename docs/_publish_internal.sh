#!/usr/bin/env sh

# Based on https://gohugo.io/hosting-and-deployment/hosting-on-github/

set -exuf -o

git config --global core.sshCommand 'ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no'
git config --global user.name "$GITHUB_GIT_USER_NAME";
git config --global user.email "$GITHUB_GIT_USER_EMAIL";
mkdir -p "${HOME}"/.ssh

echo "Fetching gh-pages branch..."
# In case of not fetched branch
# e.g. Teamcity fetches all branches only with `teamcity.git.fetchAllHeads` enabled
git fetch origin gh-pages
git show-ref origin/gh-pages

removeGhPagesWorktree() {
    echo "Deleting worktree..."
    rm -rf gh-pages-generated
    git worktree prune
}

removeGeneratedFiles() {
    echo "Removing generated files..."
    rm -rf docs/public/
    rm -rf docs/resources/_gen/
}

removeGeneratedFiles

echo "Generating Hugo site..."
hugo --cleanDestinationDir --minify --theme book --source docs

removeGhPagesWorktree

echo "Checking out gh-pages branch into temporary directory gh-pages-generated..."
git worktree add -B gh-pages gh-pages-generated origin/gh-pages

echo "Removing previously published files..."
cd gh-pages-generated
git rm -r -- *
echo "Moving generated files to gh-pages branch..."
cp -RT ../docs/public .

touch README.md
echo "These file are auto-generated by hugo. See docs/publish.sh in the develop." > README.md

git add --all
git status

echo "Pushing changes..."
# Amend history because we don't want to bloat it by generated files
git commit --amend -m "Auto-generated files" && git push --force-with-lease

cd ..
removeGeneratedFiles
removeGhPagesWorktree
