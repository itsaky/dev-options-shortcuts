{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    android-nixpkgs.url = "github:tadfisher/android-nixpkgs";
  };

  outputs = { self, nixpkgs, android-nixpkgs }:
    let
      system = "x86_64-linux";
      pkgs = nixpkgs.legacyPackages.${system};

      android-sdk = android-nixpkgs.sdk.${system} (sdkPkgs: with sdkPkgs; [
        platform-tools
        build-tools-36-0-0
        build-tools-37-0-0
        platforms-android-37-0
        platforms-android-37-2
        cmdline-tools-latest
      ]);
    in
    {
      devShells.${system}.default = pkgs.mkShell {
        buildInputs = [
          android-sdk
          pkgs.jdk21
        ];

        ANDROID_HOME = "${android-sdk}/share/android-sdk";
        GRADLE_OPTS = "-Dorg.gradle.project.android.aapt2FromMavenOverride=${android-sdk}/share/android-sdk/build-tools/37.0.0/aapt2";

        shellHook = ''
          export ANDROID_USER_HOME="$HOME/.android"
          mkdir -p "$ANDROID_USER_HOME"
        '';
      };
    };
}
