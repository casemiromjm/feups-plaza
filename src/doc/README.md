# Notes

## SSL Certificates

Our implementation generated the [keystore file](../server_keystore.p12) with:

```sh
keytool -genkeypair -alias serverkey -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore server_keystore.p12 -validity 365 -storepass password -keypass password
```

The current situation is not ideal since both server and client rely on the keystore file. Ideally the client would have only a `.cer` or `.pem` file, not the whole `.p12` file.

Also, the keystore file is only in gitlab for UC purposes.