# Scala Compatibility

## Policy

* **Scala 3** artifacts are built against the current [Scala 3 LTS](https://scala-lang.org/news/3.9/) line (currently 3.9.x).
* **Scala 2.13** artifacts are cross-published and built with the latest Scala 2.13 patch release.

## Compatibility Table

The table shows the Scala version each release was built with. For Scala 3 this is the minimum compiler version your project needs.
For Scala 2.13 use the listed patch version or a newer one.

| Version         | Scala 2 | Scala 3 |
|-----------------|---------|---------|
| 3.3.6 and later | 2.13.18 | 3.9.0   |
| 3.3.1 - 3.3.5   | 2.13.18 | 3.10.0  |
| 3.3.0           | 2.13.18 | 3.9.0   |
| 3.2.0 - 3.2.4   | 2.13.18 | 3.8.4   |
| 3.1.0 - 3.1.1   | 2.13.18 | 3.8.3   |
| 3.0.12          | 2.13.17 | 3.7.2   |
| 3.0.8 - 3.0.11  | 2.13.16 | 3.7.2   |
| 3.0.4 - 3.0.7   | 2.13.16 | 3.7.0   |
| 3.0.2 - 3.0.3   | 2.13.16 | 3.6.4   |
| 3.0.0 - 3.0.1   | 2.13.16 | 3.6.0   |

Versions before 3.0.0 have no Scala 3 support.

## Why we went back from Scala 3.10 to 3.9

Scala 3 is backward compatible, but not forward compatible: a library compiled with Scala 3.x can only be used by projects that compile with Scala 3.x or newer.

The releases 3.3.1 to 3.3.5 were built with Scala 3.10.0, a Scala Next release.
Projects on the Scala 3.9 LTS line could not use these releases.

## Scala 2

Scala 2 has no LTS line. Scala 2.13 is binary compatible across all 2.13.x patch releases, so we follow the latest 2.13 patch release.
