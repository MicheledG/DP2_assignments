
package it.polito.dp2.NFFG.sol3.client1.nffgservice;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for networkFunctionalityType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="networkFunctionalityType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CACHE"/>
 *     &lt;enumeration value="DPI"/>
 *     &lt;enumeration value="FW"/>
 *     &lt;enumeration value="MAIL_CLIENT"/>
 *     &lt;enumeration value="MAIL_SERVER"/>
 *     &lt;enumeration value="NAT"/>
 *     &lt;enumeration value="SPAM"/>
 *     &lt;enumeration value="VPN"/>
 *     &lt;enumeration value="WEB_CLIENT"/>
 *     &lt;enumeration value="WEB_SERVER"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "networkFunctionalityType")
@XmlEnum
public enum NetworkFunctionalityType {

    CACHE,
    DPI,
    FW,
    MAIL_CLIENT,
    MAIL_SERVER,
    NAT,
    SPAM,
    VPN,
    WEB_CLIENT,
    WEB_SERVER;

    public String value() {
        return name();
    }

    public static NetworkFunctionalityType fromValue(String v) {
        return valueOf(v);
    }

}
