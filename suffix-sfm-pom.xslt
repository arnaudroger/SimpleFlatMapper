<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://maven.apache.org/POM/4.0.0"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

    <xsl:param name="suffix" />
    <xsl:template match="
            node()[name() ='artifactId'
            and ((preceding-sibling::node()[name() = 'groupId' and text() = 'org.simpleflatmapper'])
            or (parent::node()[name() = 'project'])) and text() != 'ow2-asm']">
        <artifactId><xsl:value-of select="text()" />-<xsl:value-of select="$suffix" /></artifactId>
    </xsl:template>

    <xsl:template match="@* | node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>
    <!-- first copy the root and apply templates-->

</xsl:stylesheet>
