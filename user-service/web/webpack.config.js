const env = process.env.USER_SERVICE_ENV ?? (process.env.WEBPACK_SERVE ? 'dev' : 'prod');
process.env.USER_SERVICE_ENV = env;
require("dotenv").config({ path: [ `.env`, `.env.local`, `.env.${env}`, `.env.${env}.local` ], override: true });

const path = require("path");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const webpack = require("webpack");

module.exports = {
  mode: "development",
  devServer: {
    port: process.env.PORT ?? 9090,
    historyApiFallback: true,
  },
  entry: "/src/index.js",
  output: {
    path: path.resolve(__dirname, "dist"),
    publicPath: process.env.PUBLIC_PATH ?? "/",
  },
  module: {
    rules: [
      {
        test: /\.?js$/,
        exclude: /node_modules/,
        use: {
          loader: "babel-loader",
          options: {
            presets: ["@babel/preset-env", "@babel/preset-react"],
          },
        },
      },
      {
        test: /\.css$/,
        use: [
          "style-loader",
          "css-loader", // for styles
        ],
      },
    ],
  },
  plugins: [
    new HtmlWebpackPlugin({
      template: "./src/index.html", // base html
    }),
    new webpack.DefinePlugin({
      'process.env': JSON.stringify(process.env),
    }),
  ],
};