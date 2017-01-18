//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.01.18 at 03:52:33 PM CET 
//


package it.polito.dp2.NFFG.sol3.service.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="policy" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="property" type="{http://it.polito.dp2.NFFG.sol3.service.jaxb}propertyType"/>
 *                   &lt;element name="positive" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *                   &lt;element name="networkFunctionality" type="{http://it.polito.dp2.NFFG.sol3.service.jaxb}networkFunctionalityType" maxOccurs="unbounded" minOccurs="0"/>
 *                   &lt;element name="nffg" type="{http://it.polito.dp2.NFFG.sol3.service.jaxb}nameType"/>
 *                   &lt;element name="sourceNode" type="{http://it.polito.dp2.NFFG.sol3.service.jaxb}nameType"/>
 *                   &lt;element name="destinationNode" type="{http://it.polito.dp2.NFFG.sol3.service.jaxb}nameType"/>
 *                   &lt;element name="verificationResult" type="{http://it.polito.dp2.NFFG.sol3.service.jaxb}verificationResultType" minOccurs="0"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="name" type="{http://it.polito.dp2.NFFG.sol3.service.jaxb}nameType" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "policy"
})
@XmlRootElement(name = "policies")
public class Policies {

    @XmlElement(required = true)
    protected List<Policies.Policy> policy;

    /**
     * Gets the value of the policy property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the policy property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPolicy().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Policies.Policy }
     * 
     * 
     */
    public List<Policies.Policy> getPolicy() {
        if (policy == null) {
            policy = new ArrayList<Policies.Policy>();
        }
        return this.policy;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="property" type="{http://it.polito.dp2.NFFG.sol3.service.jaxb}propertyType"/>
     *         &lt;element name="positive" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
     *         &lt;element name="networkFunctionality" type="{http://it.polito.dp2.NFFG.sol3.service.jaxb}networkFunctionalityType" maxOccurs="unbounded" minOccurs="0"/>
     *         &lt;element name="nffg" type="{http://it.polito.dp2.NFFG.sol3.service.jaxb}nameType"/>
     *         &lt;element name="sourceNode" type="{http://it.polito.dp2.NFFG.sol3.service.jaxb}nameType"/>
     *         &lt;element name="destinationNode" type="{http://it.polito.dp2.NFFG.sol3.service.jaxb}nameType"/>
     *         &lt;element name="verificationResult" type="{http://it.polito.dp2.NFFG.sol3.service.jaxb}verificationResultType" minOccurs="0"/>
     *       &lt;/sequence>
     *       &lt;attribute name="name" type="{http://it.polito.dp2.NFFG.sol3.service.jaxb}nameType" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "property",
        "positive",
        "networkFunctionality",
        "nffg",
        "sourceNode",
        "destinationNode",
        "verificationResult"
    })
    public static class Policy {

        @XmlElement(required = true)
        @XmlSchemaType(name = "string")
        protected PropertyType property;
        protected boolean positive;
        @XmlSchemaType(name = "string")
        protected List<NetworkFunctionalityType> networkFunctionality;
        @XmlElement(required = true)
        protected String nffg;
        @XmlElement(required = true)
        protected String sourceNode;
        @XmlElement(required = true)
        protected String destinationNode;
        protected VerificationResultType verificationResult;
        @XmlAttribute(name = "name")
        protected String name;

        /**
         * Gets the value of the property property.
         * 
         * @return
         *     possible object is
         *     {@link PropertyType }
         *     
         */
        public PropertyType getProperty() {
            return property;
        }

        /**
         * Sets the value of the property property.
         * 
         * @param value
         *     allowed object is
         *     {@link PropertyType }
         *     
         */
        public void setProperty(PropertyType value) {
            this.property = value;
        }

        /**
         * Gets the value of the positive property.
         * 
         */
        public boolean isPositive() {
            return positive;
        }

        /**
         * Sets the value of the positive property.
         * 
         */
        public void setPositive(boolean value) {
            this.positive = value;
        }

        /**
         * Gets the value of the networkFunctionality property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the networkFunctionality property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getNetworkFunctionality().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link NetworkFunctionalityType }
         * 
         * 
         */
        public List<NetworkFunctionalityType> getNetworkFunctionality() {
            if (networkFunctionality == null) {
                networkFunctionality = new ArrayList<NetworkFunctionalityType>();
            }
            return this.networkFunctionality;
        }

        /**
         * Gets the value of the nffg property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNffg() {
            return nffg;
        }

        /**
         * Sets the value of the nffg property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNffg(String value) {
            this.nffg = value;
        }

        /**
         * Gets the value of the sourceNode property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSourceNode() {
            return sourceNode;
        }

        /**
         * Sets the value of the sourceNode property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSourceNode(String value) {
            this.sourceNode = value;
        }

        /**
         * Gets the value of the destinationNode property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDestinationNode() {
            return destinationNode;
        }

        /**
         * Sets the value of the destinationNode property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDestinationNode(String value) {
            this.destinationNode = value;
        }

        /**
         * Gets the value of the verificationResult property.
         * 
         * @return
         *     possible object is
         *     {@link VerificationResultType }
         *     
         */
        public VerificationResultType getVerificationResult() {
            return verificationResult;
        }

        /**
         * Sets the value of the verificationResult property.
         * 
         * @param value
         *     allowed object is
         *     {@link VerificationResultType }
         *     
         */
        public void setVerificationResult(VerificationResultType value) {
            this.verificationResult = value;
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

}
