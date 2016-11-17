//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.11.17 at 04:31:27 PM CET 
//


package it.polito.dp2.NFFG.sol1.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
 *         &lt;element name="catalog" type="{}catalogType"/>
 *         &lt;element name="nffgInfos">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="nffgInfo" type="{}nffgInfoType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
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
    "catalog",
    "nffgInfos"
})
@XmlRootElement(name = "nffgInfoWrapper")
public class NffgInfoWrapper {

    @XmlElement(required = true)
    protected CatalogType catalog;
    @XmlElement(required = true)
    protected NffgInfoWrapper.NffgInfos nffgInfos;

    /**
     * Gets the value of the catalog property.
     * 
     * @return
     *     possible object is
     *     {@link CatalogType }
     *     
     */
    public CatalogType getCatalog() {
        return catalog;
    }

    /**
     * Sets the value of the catalog property.
     * 
     * @param value
     *     allowed object is
     *     {@link CatalogType }
     *     
     */
    public void setCatalog(CatalogType value) {
        this.catalog = value;
    }

    /**
     * Gets the value of the nffgInfos property.
     * 
     * @return
     *     possible object is
     *     {@link NffgInfoWrapper.NffgInfos }
     *     
     */
    public NffgInfoWrapper.NffgInfos getNffgInfos() {
        return nffgInfos;
    }

    /**
     * Sets the value of the nffgInfos property.
     * 
     * @param value
     *     allowed object is
     *     {@link NffgInfoWrapper.NffgInfos }
     *     
     */
    public void setNffgInfos(NffgInfoWrapper.NffgInfos value) {
        this.nffgInfos = value;
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
     *         &lt;element name="nffgInfo" type="{}nffgInfoType" maxOccurs="unbounded"/>
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
        "nffgInfo"
    })
    public static class NffgInfos {

        @XmlElement(required = true)
        protected List<NffgInfoType> nffgInfo;

        /**
         * Gets the value of the nffgInfo property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the nffgInfo property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getNffgInfo().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link NffgInfoType }
         * 
         * 
         */
        public List<NffgInfoType> getNffgInfo() {
            if (nffgInfo == null) {
                nffgInfo = new ArrayList<NffgInfoType>();
            }
            return this.nffgInfo;
        }

    }

}
