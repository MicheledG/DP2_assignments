//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.01.10 at 11:50:22 AM CET 
//


package it.polito.dp2.NFFG.sol3.service.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for nodeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="nodeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="networkFunctionality" type="{}networkFunctionalityType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{}nameType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "nodeType", propOrder = {
    "networkFunctionality"
})
public class NodeType {

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected NetworkFunctionalityType networkFunctionality;
    @XmlAttribute(name = "name")
    protected String name;

    /**
     * Gets the value of the networkFunctionality property.
     * 
     * @return
     *     possible object is
     *     {@link NetworkFunctionalityType }
     *     
     */
    public NetworkFunctionalityType getNetworkFunctionality() {
        return networkFunctionality;
    }

    /**
     * Sets the value of the networkFunctionality property.
     * 
     * @param value
     *     allowed object is
     *     {@link NetworkFunctionalityType }
     *     
     */
    public void setNetworkFunctionality(NetworkFunctionalityType value) {
        this.networkFunctionality = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

}
