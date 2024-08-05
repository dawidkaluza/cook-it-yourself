# User service

Acts as authorization server and identity provider. Besides that, it provides UI for end users to sign in, sign up, etc.

Develop locally via docker compose. Just run _docker compose up --build_ which will run service (Rest API) and web app (UI to sign in, sign up, etc.) separately with live code reload (plus dependencies as database and so on), letting you start the development and playing with the whole service immediately.