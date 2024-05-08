declare global {
    namespace process {
        const env: {
            [key: string]: string | undefined;
            PORT: string;
            USER_SERVICE_ENV: string;
            PUBLIC_PATH: string;
            USER_SERVICE_BASEURL: string;
            SIGN_IN_PATH: string;
        }
    }
}

export {};




