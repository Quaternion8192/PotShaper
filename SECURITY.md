# Security Policy

## Supported Versions

The following versions of PotShaper are currently being supported with security updates:

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |
| < 1.0   | :x:                |

## Reporting a Vulnerability

We take the security of PotShaper seriously. If you believe you have found a security vulnerability, please report it to us as described below.

**Please do NOT report security vulnerabilities through public GitHub issues.**

Instead, please report them via email to [mail@quaternion.cc](mailto:mail@quaternion.cc) with the subject line "Security Vulnerability Report".

### What to Include

Please include the following information in your report:

- **Description**: A clear description of the vulnerability
- **Affected Version**: The version(s) of PotShaper affected
- **Impact**: The potential impact if the vulnerability is exploited
- **Reproduction Steps**: Steps to reproduce the issue (if applicable)
- **Suggested Fix**: Any suggestions for addressing the issue (optional)

### Response Timeline

You should receive an acknowledgment within **48 hours** confirming receipt of your report. We will then work to assess the severity and determine a remediation plan.

### Disclosure Policy

We follow a coordinated disclosure process:

1. **Initial Report**: You submit a vulnerability report to us
2. **Assessment**: We evaluate the report and confirm the vulnerability
3. **Remediation**: We develop and test a fix
4. **Release**: We release a patched version
5. **Disclosure**: After **90 days** from the initial report (or sooner if critical), we publicly disclose the vulnerability

In cases where the vulnerability is critical, we may accelerate this timeline.

## Security Best Practices

When using PotShaper, please follow these security best practices:

### 1. Keep Dependencies Updated

PotShaper depends on Apache POI and other libraries. Ensure you are using the latest versions of all dependencies to benefit from security patches.

```xml
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi</artifactId>
    <version>5.4.0</version> <!-- Use latest version -->
</dependency>
```

### 2. Validate Input Files

When opening PowerPoint files from untrusted sources:

- Always validate the file source
- Scan files with antivirus software before processing
- Be cautious with presentations containing macros or embedded content

### 3. Memory Management

Large presentations can consume significant memory. Monitor memory usage:

```java
PotPresentation ppt = PotPresentation.open("large-file.pptx");
ppt.setLargeFileWarning(500, stats -> {
    System.out.println("Memory warning: " + stats.getEstimatedMemoryMB() + " MB");
});
```

### 4. Resource Cleanup

Always close presentations properly to prevent resource leaks:

```java
try (PotPresentation ppt = PotPresentation.open("file.pptx")) {
    // Work with presentation
} catch (Exception e) {
    // Handle exceptions
}
```

### 5. Exception Handling

PotShaper throws `PotException` for error conditions. Implement proper exception handling:

```java
try {
    PotPresentation ppt = PotPresentation.open("file.pptx");
    // ... operations
    ppt.close();
} catch (PotException e) {
    // Log and handle appropriately
    logger.error("Failed to process presentation", e);
}
```

## Known Security Considerations

### Apache POI Dependencies

PotShaper inherits security considerations from Apache POI. Please review:
- [Apache POI Security Advisories](https://poi.apache.org/security.html)
- [Apache Security Team](https://www.apache.org/security/)

### File Format Limitations

- Only supports Office Open XML format (.pptx), not binary format (.ppt)
- Embedded OLE objects may pose security risks - handle with caution

### Network Resources

When working with presentations containing external links or embedded media:

- Verify network connectivity requirements
- Be aware of potential SSRF (Server-Side Request Forgery) risks
- Validate external resource URLs

## Security Updates

Security updates will be released as patch versions (e.g., 1.0.1, 1.0.2). Critical security fixes may be released outside the normal release cycle.

Stay informed about security updates by:
- Watching the [GitHub repository](https://github.com/quaternion8192/potshaper)
- Checking the [CHANGELOG](CHANGELOG) for security-related fixes
- Following [Apache POI security advisories](https://poi.apache.org/security.html)

## Contact

For security-related questions or concerns:

- **Email**: [mail@quaternion.cc](mailto:mail@quaternion.cc)
- **GitHub**: https://github.com/quaternion8192/potshaper

---

**Last Updated**: March 12, 2026  
**Version**: 1.0
