if (config.devServer) {
    config.devServer.port = 3000;
    config.devServer.proxy = [
        {
            context: ['/api'],
            target: 'http://localhost:8080',
        },
    ];
}
