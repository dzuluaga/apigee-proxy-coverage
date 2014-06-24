<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/">
        <html lang="en">
            <head>
                <meta charset="utf-8"/>
                <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
                <meta name="viewport" content="width=device-width, initial-scale=1"/>
                <title>Apigee Proxy Coverage Report</title>
                <link href="bootstrap.min.css" rel="stylesheet"/>
            </head>
            <body>
                <section id="heading">
                    <div class="well">
                        <div class="row">
                            <div class="col-lg-3">
                                <h4>
                                    <i>
                                        Proxy:
                                        <xsl:value-of select="ProxyEndpoint/@name"/>
                                        <xsl:value-of select="TargetEndpoint/@name"/>
                                    </i>
                                </h4>
                            </div>
                            <div class="col-lg-9" align="right">
                                <h6>
                                    <i>
                                        Policy Execution Stats
                                    </i>
                                </h6>
                            </div>
                        </div>
                    </div>
                </section>

                <section id="Execution Details">
                    <div class="container">
                        <xsl:apply-templates select="//PreFlow"/>
                        <xsl:apply-templates select="//PostFlow"/>
                        <xsl:for-each select="//FaultRules/FaultRule">
                            <xsl:apply-templates select="."/>
                        </xsl:for-each>
                        <xsl:for-each select="//Flows/Flow">
                            <xsl:apply-templates select="."/>
                        </xsl:for-each>
                    </div>
                </section>

                <section id="footer">
                    <div class="page-header"></div>
                    <div class="container">
                        <div class="row">
                            <div class="col-lg-12">
                                <div class="col-lg-12">
                                    <ul class="list-unstyled">
                                        <li class="pull-right">
                                            <a href="#top">Back to top</a>
                                        </li>
                                    </ul>
                                    <p>Implemented by <a href="mailto:sriki77@gmail.com" rel="nofollow">Srikanth
                                        Seshadri</a>.
                                    </p>

                                    <p>Code released under the <a
                                            href="https://github.com/sriki77/apigee-proxy-coverage/blob/master/LICENSE">
                                        MIT License</a>.
                                        Based on <a href="http://getbootstrap.com" rel="nofollow">Bootstrap</a>.
                                        Using <a href="http://bootswatch.com/readable/" rel="nofollow">Bootswatch
                                            Readable Theme</a>.
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>
                </section>
            </body>
        </html>
    </xsl:template>
    <xsl:template match="PreFlow|PostFlow|Flow">
        <div class="row">
            <div class="col-lg-12">
                <a>
                    <xsl:attribute name="name">
                        <xsl:value-of select="@name"/>
                    </xsl:attribute>
                </a>
                <table class="table">
                    <caption>
                        <i>
                            Flow:
                            <xsl:value-of select="@name"/>
                        </i>
                    </caption>
                    <thead>
                        <tr>
                            <th><small>Policy Name</small></th>
                            <th><small>Condition</small></th>
                        </tr>
                    </thead>
                    <tbody>
                        <xsl:if test="Request/Step">
                            <tr class="text-primary">
                                <td colspan="2">
                                <small>Request Flow</small>
                                </td>
                            </tr>
                            <xsl:apply-templates select="Request"/>
                        </xsl:if>
                        <xsl:if test="Response/Step">
                            <tr class="text-primary">
                                <td colspan="2">
                                <small>Response Flow</small>
                                </td>
                            </tr>
                            <xsl:apply-templates select="Response"/>
                        </xsl:if>
                    </tbody>
                </table>
            </div>
        </div>
    </xsl:template>
    <xsl:template match="FaultRule">
        <div class="row">
            <div class="col-lg-12">
                <a>
                    <xsl:attribute name="name">
                        <xsl:value-of select="@name"/>
                    </xsl:attribute>
                </a>
                <table class="table">
                    <caption>
                        <i>
                            Fault Rule:
                            <xsl:value-of select="@name"/>
                        </i>
                    </caption>
                    <thead>
                        <tr>
                            <th><small>Policy Name</small></th>
                            <th><small>Condition</small></th>
                        </tr>
                    </thead>
                    <tbody>
                        <xsl:for-each select="Step">
                            <xsl:apply-templates select="."/>
                        </xsl:for-each>
                    </tbody>
                </table>
            </div>
        </div>
    </xsl:template>
    <xsl:template match="Request|Response">
        <xsl:for-each select="Step">
            <xsl:apply-templates select="."/>
        </xsl:for-each>
    </xsl:template>
    <xsl:template match="Step">
        <tr>
            <xsl:if test="@executed='true'">
                <xsl:attribute name="class">success</xsl:attribute>
            </xsl:if>
            <td>
                <small>
                    <xsl:value-of select="Name"/>
                </small>
            </td>
            <td>
                <xsl:choose>
                    <xsl:when test="Condition">
                        <small>
                            <xsl:value-of select="Condition"/>
                        </small>
                    </xsl:when>
                    <xsl:otherwise>
                        -
                    </xsl:otherwise>
                </xsl:choose>
            </td>
        </tr>
    </xsl:template>
</xsl:stylesheet>