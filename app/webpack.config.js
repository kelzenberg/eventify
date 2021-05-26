const webpack = require('webpack');
const path = require("path");
const CopyPlugin = require("copy-webpack-plugin");

let config = {
    target: "web",
    entry: {
        welcome: "./entries/welcome/welcome.index.js",
        main: "./entries/main/main.index.js"
    },
    output: {
        filename: "[name]/bundle.js",
        path: path.resolve(__dirname, "dist"),
        publicPath: "/" // needs to be the same as devServer.publicPath
    },
    module: {
        rules: [
            {
                test: /\.(js|jsx)$/,
                exclude: /node_modules/,
                use: ['babel-loader'],
            },
            {
                test: /\.css$/,
                use: ["style-loader", "css-loader", "postcss-loader"]
            },
            { 
                test: /\.(scss)$/,
                use: ["style-loader", "css-loader?-url", "postcss-loader", "sass-loader"]
            }
        ]
    },
    plugins: [
        new CopyPlugin({
            patterns: [
                { from: "entries/main/main.index.html", to: "main/index.html" },
                { from: "entries/welcome/welcome.index.html", to: "welcome/index.html" },
                { from: "assets/**", to: "" },
                { from: "assets/favicon/favicon.ico", to: "favicon.ico" }
            ],
        }),
    ],
    devServer: {
        contentBase: path.join(__dirname, "dist"),
        publicPath: "/", // needs to be the same as output.publicPath
        host: '127.0.0.1',
        port: 8081,
        hot: true,
        historyApiFallback: {
            rewrites: [
                { from: /^\/you.*$/, to: 'main/index.html' },
                { from: /^\/.*$/, to: 'welcome/index.html' }
            ]
        }, // for react router to work,
        clientLogLevel: 'warn',
    },
};

module.exports = (env, argv) => {
    if(argv.mode == 'development') {
        // development mode
        config.plugins.push(new webpack.HotModuleReplacementPlugin());
    } else {
        // production mode
        
    }

    return config;
};