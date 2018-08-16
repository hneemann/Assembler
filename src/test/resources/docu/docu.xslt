<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format">

    <xsl:template match="root">
        <fo:root font-family="SansSerif" font-size="12pt" xml:lang="en">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="DIN-A4"
                                       page-height="29.7cm" page-width="21cm"
                                       margin-top="2cm"     margin-bottom="1cm"
                                       margin-left="2.5cm"  margin-right="2.5cm">
                    <fo:region-body
                            margin-top="1.3cm" margin-bottom="1.8cm"
                            margin-left="0cm"  margin-right="0cm"/>
                    <fo:region-before region-name="header" extent="1.3cm"/>
                    <fo:region-after  region-name="footer" extent="1.5cm"/>
                    <fo:region-start  region-name="left"   extent="0cm"/>
                    <fo:region-end    region-name="right"  extent="0cm"/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="DIN-A4">
                <fo:static-content flow-name="footer">
                    <fo:block text-align-last="justify" border-top-style="solid">
                        <fo:inline text-align="left" font-size="8pt">
                            <fo:basic-link external-destination="https://github.com/hneemann/Assembler" show-destination="new">
                                https://github.com/hneemann/Assembler
                            </fo:basic-link>
                        </fo:inline>
                        <fo:leader/>
                        <fo:inline text-align="right">
                            <fo:page-number/> / <fo:page-number-citation ref-id="LastPage"/>
                        </fo:inline>
                    </fo:block>
                </fo:static-content>
                <fo:flow flow-name="xsl-region-body">
                    <!-- large title -->
                    <fo:block font-size="16pt" font-weight="bold">
                        Instruction Set Summary
                    </fo:block>

                    <fo:block font-weight="bold" margin-top="1cm">
                        Instructions
                    </fo:block>
                    <fo:table table-layout="fixed" margin-top="5mm" width="100%">
                        <fo:table-column column-number="2" column-width="4.4cm"/>
                        <fo:table-column column-number="1" column-width="1.1cm"/>
                        <fo:table-column column-number="3" column-width="10.4cm"/>
                        <fo:table-header>
                            <fo:table-row>
                                <fo:table-cell column-number="2" border-style="solid" border-width="0pt" border-after-width="1pt">
                                    <fo:block text-align="left">Instruction</fo:block>
                                </fo:table-cell>
                                <fo:table-cell column-number="1" border-style="solid" border-width="0pt" border-after-width="1pt">
                                    <fo:block text-align="left">Op</fo:block>
                                </fo:table-cell>
                                <fo:table-cell column-number="3" border-style="solid" border-width="0pt" border-after-width="1pt">
                                    <fo:block text-align="left">Description</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-header>
                        <fo:table-body>
                            <xsl:apply-templates select="opcode"/>
                        </fo:table-body>
                    </fo:table>
                    <fo:block font-weight="bold" margin-top="1cm">
                        Macros
                    </fo:block>
                    <fo:table table-layout="fixed" margin-top="5mm" width="100%">
                        <fo:table-column column-number="1" column-width="5.4cm"/>
                        <fo:table-column column-number="2" column-width="10.4cm"/>
                        <fo:table-header>
                            <fo:table-row>
                                <fo:table-cell column-number="1" border-style="solid" border-width="0pt" border-after-width="1pt">
                                    <fo:block text-align="left">Macro</fo:block>
                                </fo:table-cell>
                                <fo:table-cell column-number="2" border-style="solid" border-width="0pt" border-after-width="1pt">
                                    <fo:block text-align="left">Description</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-header>
                        <fo:table-body>
                            <xsl:apply-templates select="macro"/>
                        </fo:table-body>
                    </fo:table>
                    <fo:block font-weight="bold" margin-top="1cm">
                        Directives
                    </fo:block>
                    <fo:table table-layout="fixed" margin-top="5mm" width="100%">
                        <fo:table-column column-number="1" column-width="5.4cm"/>
                        <fo:table-column column-number="2" column-width="10.4cm"/>
                        <fo:table-header>
                            <fo:table-row>
                                <fo:table-cell column-number="1" border-style="solid" border-width="0pt" border-after-width="1pt">
                                    <fo:block text-align="left">Directive</fo:block>
                                </fo:table-cell>
                                <fo:table-cell column-number="2" border-style="solid" border-width="0pt" border-after-width="1pt">
                                    <fo:block text-align="left">Description</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-header>
                        <fo:table-body>
                            <xsl:apply-templates select="dir"/>
                        </fo:table-body>
                    </fo:table>
                    <fo:block id="LastPage"/>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>

    <!-- Creation of the table of content-->
    <xsl:template match="opcode">
        <fo:table-row>
            <fo:table-cell column-number="2" padding-before="1.5mm">
                <fo:block text-align="left" font-weight="bold"><xsl:value-of select="@name"/></fo:block>
            </fo:table-cell>
            <fo:table-cell column-number="1" padding-before="1.5mm">
                <fo:block text-align="left"><xsl:value-of select="@opcode"/></fo:block>
            </fo:table-cell>
            <fo:table-cell column-number="3" padding-before="1.5mm">
                <fo:block text-align="left"><xsl:value-of select="."/></fo:block>
            </fo:table-cell>
        </fo:table-row>
    </xsl:template>

    <xsl:template match="macro">
        <fo:table-row>
            <fo:table-cell column-number="1" padding-before="1.5mm">
                <fo:block text-align="left" font-weight="bold"><xsl:value-of select="@name"/></fo:block>
            </fo:table-cell>
            <fo:table-cell column-number="2" padding-before="1.5mm">
                <fo:block text-align="left"><xsl:value-of select="."/></fo:block>
            </fo:table-cell>
        </fo:table-row>
    </xsl:template>

    <xsl:template match="dir">
        <fo:table-row>
            <fo:table-cell column-number="1" padding-before="1.5mm">
                <fo:block text-align="left" font-weight="bold"><xsl:value-of select="@name"/></fo:block>
            </fo:table-cell>
            <fo:table-cell column-number="2" padding-before="1.5mm">
                <fo:block text-align="left"><xsl:value-of select="."/></fo:block>
            </fo:table-cell>
        </fo:table-row>
    </xsl:template>

</xsl:stylesheet>
