package com.epam.esm.persistence.model.entity;

import com.epam.esm.persistence.audit.EntityAuditListener;
import com.epam.esm.persistence.query.NativeQuery;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedNativeQuery(
        name = "findUserMostWidelyUsedTagWithHighestOrderCost",
        query = NativeQuery.MOST_WIDELY_USED_WITH_HIGHEST_ORDER_COST_TAG_QUERY,
        resultSetMapping = "bestTagMapping"
)
@EntityListeners(EntityAuditListener.class)
@Table(name = "tags")
public class Tag extends AbstractEntity{

    @Column(name="name", length = 60, nullable = false, unique = true)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "tags")
    private List<GiftCertificate> certificates = new ArrayList<>();

    public Tag() {
    }

    public Tag(long id, String name) {
        setId(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<GiftCertificate> getCertificates() {
        return certificates;
    }

    public void setCertificates(List<GiftCertificate> certificates) {
        this.certificates = certificates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Tag tag = (Tag) o;

        return name.equals(tag.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Tag{" +
                "name='" + name + '\'' +
                "}";
    }
}
