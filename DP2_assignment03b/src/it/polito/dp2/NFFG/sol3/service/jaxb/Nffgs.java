//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.01.19 at 12:18:46 PM CET 
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
import javax.xml.datatype.XMLGregorianCalendar;


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
 *         &lt;element name="nffg" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="nodes">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="node" type="{http://it.polito.dp2.NFFG.sol3.service.jaxb}nodeType" maxOccurs="unbounded" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="links">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="link" type="{http://it.polito.dp2.NFFG.sol3.service.jaxb}linkType" maxOccurs="unbounded" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="lastUpdate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
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
    "nffg"
})
@XmlRootElement(name = "nffgs")
public class Nffgs {

    @XmlElement(required = true)
    protected List<Nffgs.Nffg> nffg;

    /**
     * Gets the value of the nffg property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nffg property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNffg().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Nffgs.Nffg }
     * 
     * 
     */
    public List<Nffgs.Nffg> getNffg() {
        if (nffg == null) {
            nffg = new ArrayList<Nffgs.Nffg>();
        }
        return this.nffg;
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
     *         &lt;element name="nodes">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="node" type="{http://it.polito.dp2.NFFG.sol3.service.jaxb}nodeType" maxOccurs="unbounded" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="links">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="link" type="{http://it.polito.dp2.NFFG.sol3.service.jaxb}linkType" maxOccurs="unbounded" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="lastUpdate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
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
        "nodes",
        "links",
        "lastUpdate"
    })
    public static class Nffg {

        @XmlElement(required = true)
        protected Nffgs.Nffg.Nodes nodes;
        @XmlElement(required = true)
        protected Nffgs.Nffg.Links links;
        @XmlElement(required = true)
        @XmlSchemaType(name = "dateTime")
        protected XMLGregorianCalendar lastUpdate;
        @XmlAttribute(name = "name")
        protected String name;

        /**
         * Gets the value of the nodes property.
         * 
         * @return
         *     possible object is
         *     {@link Nffgs.Nffg.Nodes }
         *     
         */
        public Nffgs.Nffg.Nodes getNodes() {
            return nodes;
        }

        /**
         * Sets the value of the nodes property.
         * 
         * @param value
         *     allowed object is
         *     {@link Nffgs.Nffg.Nodes }
         *     
         */
        public void setNodes(Nffgs.Nffg.Nodes value) {
            this.nodes = value;
        }

        /**
         * Gets the value of the links property.
         * 
         * @return
         *     possible object is
         *     {@link Nffgs.Nffg.Links }
         *     
         */
        public Nffgs.Nffg.Links getLinks() {
            return links;
        }

        /**
         * Sets the value of the links property.
         * 
         * @param value
         *     allowed object is
         *     {@link Nffgs.Nffg.Links }
         *     
         */
        public void setLinks(Nffgs.Nffg.Links value) {
            this.links = value;
        }

        /**
         * Gets the value of the lastUpdate property.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getLastUpdate() {
            return lastUpdate;
        }

        /**
         * Sets the value of the lastUpdate property.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setLastUpdate(XMLGregorianCalendar value) {
            this.lastUpdate = value;
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
         *         &lt;element name="link" type="{http://it.polito.dp2.NFFG.sol3.service.jaxb}linkType" maxOccurs="unbounded" minOccurs="0"/>
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
            "link"
        })
        public static class Links {

            protected List<LinkType> link;

            /**
             * Gets the value of the link property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the link property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getLink().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link LinkType }
             * 
             * 
             */
            public List<LinkType> getLink() {
                if (link == null) {
                    link = new ArrayList<LinkType>();
                }
                return this.link;
            }

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
         *         &lt;element name="node" type="{http://it.polito.dp2.NFFG.sol3.service.jaxb}nodeType" maxOccurs="unbounded" minOccurs="0"/>
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
            "node"
        })
        public static class Nodes {

            protected List<NodeType> node;

            /**
             * Gets the value of the node property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the node property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getNode().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link NodeType }
             * 
             * 
             */
            public List<NodeType> getNode() {
                if (node == null) {
                    node = new ArrayList<NodeType>();
                }
                return this.node;
            }

        }

    }

}
