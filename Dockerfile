FROM node:18-alpine as build

WORKDIR /reactapp

COPY package*.json ./

RUN npm install

COPY . .

RUN npm run build

FROM nginx:alpine

COPY --from=build /reactapp/build/ /usr/share/nginx/html