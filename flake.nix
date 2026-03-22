{
  description = "IBAN validation service dev environment";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs { inherit system; };
      in
      {
        devShells.default = pkgs.mkShell {
          packages = with pkgs; [
            # Java
            jdk25

            # Frontend
            nodejs_24

            # Containers
            podman

            # Load testing
            wrk

            # Utilities
            gnumake
          ];

          shellHook = ''
            export JAVA_HOME=${pkgs.jdk25}

            # Testcontainers with Podman
            export DOCKER_HOST="unix:///run/user/$(id -u)/podman/podman.sock"
            export TESTCONTAINERS_RYUK_DISABLED=true
          '';
        };
      });
}
