
package it.polito.dp2.NFFG.sol3.client1.nffgservice;

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
 *         &lt;element name="entityPointer" type="{http://it.polito.dp2.NFFG.sol3.service.jaxb}entityPointerType" maxOccurs="unbounded"/>
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
    "entityPointer"
})
@XmlRootElement(name = "entityPointers")
public class EntityPointers {

    @XmlElement(required = true)
    protected List<EntityPointerType> entityPointer;

    /**
     * Gets the value of the entityPointer property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the entityPointer property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEntityPointer().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EntityPointerType }
     * 
     * 
     */
    public List<EntityPointerType> getEntityPointer() {
        if (entityPointer == null) {
            entityPointer = new ArrayList<EntityPointerType>();
        }
        return this.entityPointer;
    }

}
