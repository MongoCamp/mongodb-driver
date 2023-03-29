#!/usr/bin/env sh

# abort on errors
set -e

# build
pnpm install

# build
pnpm docs:build

# navigate into the build output directory
cd docs/.vitepress/dist

# if you are deploying to a custom domain
echo 'server.mongocamp.dev' > CNAME

git init
git add -A
git commit -m 'docs: deploy documentation'


# if you are deploying to https://<USERNAME>.github.io/<REPO>
git push -f https://$GITHUB_USER:$GITHUB_TOKEN@github.com/mongocamp/mongocamp-server.git  master:gh-pages

cd -
