
package it.polito.dp2.NFFG.sol3.client2.nffgservice;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for propertyType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="propertyType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="REACHABILITY"/>
 *     &lt;enumeration value="TRAVERSAL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "propertyType")
@XmlEnum
public enum PropertyType {

    REACHABILITY,
    TRAVERSAL;

    public String value() {
        return name();
    }

    public static PropertyType fromValue(String v) {
        return valueOf(v);
    }

}
