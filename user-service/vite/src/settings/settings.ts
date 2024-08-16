const publicPath: string = import.meta.env.VITE_PUBLIC_PATH ?? "";
const userServiceUrl: string = import.meta.env.VITE_USER_SERVICE_URL ?? "";

export const settings = { publicPath, userServiceUrl };