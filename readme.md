# Welcome to Blog App (API)!

Blog app backend *REST API* is built with **Ktor** framework with **H2** as database and deployed on **[Railway](https://railway.app/)**.
Currently the api is deployed on *web-production-147b.up.railway.app [link](https://web-production-147b.up.railway.app)*



### Setup Localdb

1. install postgresql `brew install postgresql` (i'm using 14.17) 
2. start the psql service `brew services start postgresql`
3. connect to the default db `psql postgres`
4. create db: `create database flock_db`

Your user should be your mac user