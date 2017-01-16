
package it.polito.dp2.NFFG.sol3.client2.nffgservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for verificationResultType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="verificationResultType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="satisfied" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="lastVerification" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "verificationResultType", propOrder = {
    "satisfied",
    "lastVerification",
    "description"
})
public class VerificationResultType {

    protected boolean satisfied;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastVerification;
    @XmlElement(required = true)
    protected String description;

    /**
     * Gets the value of the satisfied property.
     * 
     */
    public boolean isSatisfied() {
        return satisfied;
    }

    /**
     * Sets the value of the satisfied property.
     * 
     */
    public void setSatisfied(boolean value) {
        this.satisfied = value;
    }

    /**
     * Gets the value of the lastVerification property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastVerification() {
        return lastVerification;
    }

    /**
     * Sets the value of the lastVerification property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastVerification(XMLGregorianCalendar value) {
        this.lastVerification = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

}
