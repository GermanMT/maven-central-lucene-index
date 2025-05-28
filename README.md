# Maven Central Lucene Index

This repository provides tools and utilities for working with the Maven Central repository index using **Apache Lucene**. Due to the current Maven Central guide uses outdated versions, this repository was born out of the need to create a simple guide on how to build the Lucene index for **Maven Central** updated to **27 May 2025**. It allows you to create, deploy, and query the Maven Central index to extract information about available artifacts and their dependencies. This repository has the following features:
> ⚙️ Create and maintain a searchable index of Maven Central artifacts
> ⚙️ Interactive exploration of the index using Luke
> ⚙️ Extract artifact information in a standardized format
> ⚙️ Support for full-text search across Maven artifacts
> ⚙️ Efficient querying of artifact metadata

## Requirements

- Java Development Kit (JDK) version 21.0.7 with support for incubator modules.
- Apache Lucene 10.2.1.
- Maven Central Repository Index.
- Indexer CLI 7.1.5.

## How to create the index

The following steps will guide you through the process of creating a searchable Lucene index from the Maven Central Repository index. This index will allow you to efficiently search and query Maven artifacts.

1. Download the [nexus-maven-repository-index.gz](https://repo.maven.apache.org/maven2/.index/nexus-maven-repository-index.gz) file.

2. Create the Lucene index using indexer 7.1.5 with the command ```java --add-modules jdk.incubator.vector --enable-native-access=ALL-UNNAMED -jar lib/indexer-cli-7.1.5-cli.jar --unpack nexus-maven-repository-index.gz --destination central-lucene-index --type full```.

3. The index creation process takes approximately 40 minutes, with the compressed file dated 27 May 2025. As that file grows in size, it is understood that it could take longer.

## How to deploy Lucene

Once you have created the Lucene index, you'll need to set up the necessary tools to interact with it. The following steps will help you deploy and access the index using Luke, a powerful GUI tool for exploring and managing Lucene indexes.

4. Download the [lucene-10.2.1.tgz](https://dlcdn.apache.org/lucene/java/10.2.1/lucene-10.2.1.tgz) file and extract it in the project root.

5. Run the command ```sh lucene-10.2.1/bin/luke.sh```. This command will allow you to use the index interactively with a graphical interface. Opening the index may take a few minutes.

## How to use the Index for listing artifacts

We can generate a file where each line specifies a Maven artifact in the following format ```<group_id>:<artifact_id>:<version>```. To do this, follow these steps:

1. Compile the MavenArtifactExtractor.java file using the command ```javac -cp "lib/*" MavenArtifactExtractor.java```.

2. Run the file using the command ```java -cp ".:lib/*" MavenArtifactExtractor```.
