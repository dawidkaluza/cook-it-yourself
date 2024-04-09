package pl.dkaluza.userservice.adapters.in.web;

record SignUpRequest(String email, char[] password, String name) {
}
