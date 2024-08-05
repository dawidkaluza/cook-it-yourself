# Web app

Browser-based UI for end users.

To develop the app locally, run all the apps on Kubernetes as explained in main README file, but don't forward local port 3000. Instead of using deployed withing K8s web-app, run it locally in development mode via _npm run dev_ command. With this setup, you will be able to use backend services while developing the app without any extra configuration within K8s and the apps deployed there.