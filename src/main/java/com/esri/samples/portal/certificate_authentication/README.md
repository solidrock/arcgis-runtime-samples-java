# Certificate authentication with PKI

Access secured portals using a certificate.

![](CertificateAuthenticationWithPKI.jpg)

## Use case

PKI (Public Key Infrastructure) is a certificate authentication method to secure resources without requiring users to remember passwords. Government agencies commonly issue smart cards using PKI to access computer systems.

## How to use the sample

> **NOTE**: You must provide your own ArcGIS Portal with PKI authentication configured.

Provide a URL to a PKI-enabled server and a path to a PKI certificate for that server. When prompted, provide the password for the certificate, and select 'Authenticate' to connect to the portal and load the contents.

## How it works

1. Use a path to a 'pfx' or 'p12' certificate and its password to create a `CertificateCredential`
2. Create a custom `AuthenticationChallengeHandler` that handles challenges of `AuthenticationChallenge.Type.CERTIFICATE_CHALLENGE`, and responds with the created certificate credential.
3. Set up the `AuthenticationManager` to use the created authentication challenge handler.
4. Create a `Portal` from a URL to a resource secured by PKI authentication, and the authentication manager will resolve the authentication challenges.

## Relevant API

* AuthenticationChallengeHandler
* AuthenticationManager
* CertificateCredential
* Portal

## Additional information

ArcGIS Enterprise requires special configuration to enable support for PKI. PKI authentication can be set up to work with accounts managed by [Windows Active Directory](https://enterprise.arcgis.com/en/portal/latest/administer/windows/using-windows-active-directory-and-pki-to-secure-access-to-your-portal.htm) or [LDAP](https://enterprise.arcgis.com/en/portal/latest/administer/windows/use-ldap-and-pki-to-secure-access-to-your-portal.htm).

## Tags

smartcard, PKI, certificate, store, X509, authentication, login, passwordless