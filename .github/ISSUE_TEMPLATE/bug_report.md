---
name: Bug Report
description: File a bug report
title: "[BUG] "
labels: ["bug"]
assignees: []
body:
  - type: markdown
    attributes:
      value: |
        Thanks for taking the time to fill out this bug report!
  - type: input
    id: version
    attributes:
      label: PotShaper Version
      description: What version of PotShaper are you using?
      placeholder: "e.g., 1.0.0"
    validations:
      required: true
  - type: input
    id: java-version
    attributes:
      label: Java Version
      description: What Java version are you using?
      placeholder: "e.g., JDK 17.0.2"
    validations:
      required: true
  - type: textarea
    id: what-happened
    attributes:
      label: What happened?
      description: Also tell us, what did you expect to happen?
      placeholder: Tell us what you see!
      value: "A bug happened!"
    validations:
      required: true
  - type: textarea
    id: steps-to-reproduce
    attributes:
      label: Steps to Reproduce
      description: Steps to reproduce the behavior
      placeholder: |
        1. Create a PotPresentation...
        2. Add a slide...
        3. Call method...
        4. See error
    validations:
      required: true
  - type: textarea
    id: code-example
    attributes:
      label: Code Example
      description: Please provide a minimal code example that reproduces the issue
      render: java
  - type: textarea
    id: logs
    attributes:
      label: Relevant Log Output
      description: Please copy and paste any relevant log output
      render: shell
  - type: checkboxes
    id: terms
    attributes:
      label: Code of Conduct
      description: By submitting this issue, you agree to follow our Code of Conduct
      options:
        - label: I agree to follow this project's Code of Conduct
          required: true
