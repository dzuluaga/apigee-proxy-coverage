<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/">
        <html lang="en">
            <head>
                <meta charset="utf-8"/>
                <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
                <meta name="viewport" content="width=device-width, initial-scale=1"/>
                <title>Apigee Proxy Coverage Report</title>
                <link href="bootstrap/css/bootstrap.min.css" rel="stylesheet"/>
                <!--<script src="bootstrap/js/html5shiv.js"></script>-->
                <!--<script src="bootstrap/js/respond.min.js"></script>-->
                <!--<script src="bootstrap/js/jquery.min.js"></script>-->
                <!--<script src="bootstrap/js/bootstrap.min.js"></script>-->
            </head>
            <body>
                <section id="heading">
                    <div class="well">
                        <div class="row">
                            <div class="col-lg-3">
                                <h4>
                                    <i>
                                        <xsl:value-of select="ProxyStat/name"/>
                                    </i>

                                </h4>
                            </div>
                            <div class="col-lg-3" style="padding-top: 12px;">
                                Coverage
                                <xsl:apply-templates select="ProxyStat/coverage"/>
                            </div>
                            <div class="col-lg-3" style="padding-top: 12px;">
                                Total Policies:
                                <xsl:value-of select="ProxyStat/totalPolicies"/>
                            </div>
                            <div class="col-lg-3" style="padding-top: 12px;">
                                Total Policies Executed:
                                <xsl:value-of select="ProxyStat/executedPolicies"/>

                            </div>
                        </div>
                    </div>
                </section>
                <section id="Coverage Details">
                    <div class="container">
                        <div class="text-warning">
                            <h4>Proxy Endpoints</h4>
                        </div>
                        <xsl:for-each select="//EndpointStat/endpointType[text() = 'Proxy']">
                            <div class="row">
                                <xsl:apply-templates select="parent::node()"/>
                            </div>
                        </xsl:for-each>
                    </div>

                    <div class="container">
                        <div class="text-warning">
                            <h4>Target Endpoints</h4>
                        </div>
                        <xsl:for-each select="//EndpointStat/endpointType[text() = 'Target']">
                            <div class="row">
                                <xsl:apply-templates select="parent::node()"/>
                            </div>
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
    <xsl:template match="EndpointStat">
        <div class="col-lg-12">
            <table class="table table-hover">
                <caption>
                    <i>Endpoint:
                        <xsl:value-of select="name"/>
                    </i>
                </caption>
                <thead>
                    <tr>
                        <th>Flow Name</th>
                        <th>Coverage</th>
                        <th>Total</th>
                        <th>Executed</th>
                    </tr>
                </thead>
                <tbody>
                    <tr class="text-info">
                        <td>
                            <a>
                                <xsl:attribute name="href">
                                    <xsl:value-of select="endpointType"/>_<xsl:value-of select="name"/>.html
                                </xsl:attribute>
                                Endpoint Overall
                            </a>
                        </td>
                        <td>
                            <xsl:apply-templates select="coverage"/>
                        </td>
                        <td>
                            <xsl:value-of select="totalPolicies"/>
                        </td>
                        <td>
                            <xsl:value-of select="executedPolicies"/>
                        </td>
                    </tr>
                    <xsl:for-each select="stats/FlowStat">
                        <tr>
                            <td>
                                <a>
                                    <xsl:attribute name="href">
                                        <xsl:value-of select="ancestor::EndpointStat/endpointType"/>_<xsl:value-of
                                            select="ancestor::EndpointStat/name"/>.html#<xsl:choose>
                                            <xsl:when test="name = ''">
                                                <xsl:value-of select="flowType"/>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <xsl:value-of select="name"/>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </xsl:attribute>
                                    <xsl:choose>
                                        <xsl:when test="name = ''">
                                            <xsl:value-of select="flowType"/>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            Flow:
                                            <xsl:value-of select="name"/>
                                        </xsl:otherwise>
                                    </xsl:choose>

                                </a>
                            </td>
                            <td>
                                <xsl:apply-templates select="coverage"/>
                            </td>
                            <td>
                                <xsl:value-of select="totalPolicies"/>
                            </td>
                            <td>
                                <xsl:value-of select="executedPolicies"/>
                            </td>
                        </tr>
                    </xsl:for-each>
                </tbody>
            </table>
        </div>
    </xsl:template>
    <xsl:template match="coverage">
        <xsl:if test="current() &lt; 50">
            <span class="text-danger">
                <xsl:value-of select="current()"/>%
            </span>
        </xsl:if>
        <xsl:if test="current() = 100">
            <span class="text-success">
                <xsl:value-of select="current()"/>%
            </span>
        </xsl:if>
        <xsl:if test="current() &lt; 100 and current() &gt; 49">
            <span class="text-warning">
                <xsl:value-of select="current()"/>%
            </span>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>