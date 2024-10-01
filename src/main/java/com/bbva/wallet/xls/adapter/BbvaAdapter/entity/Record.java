package com.bbva.wallet.xls.adapter.BbvaAdapter.entity;

import com.bbva.wallet.xls.adapter.BbvaAdapter.entity.Generator.RecordIdGenerator;
import com.bbva.wallet.xls.adapter.BbvaAdapter.util.Util;
import jakarta.persistence.*;
import lombok.*;
import org.apache.logging.log4j.util.Strings;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "record")
public class Record {

    @Id
    @GeneratedValue(generator = "id-generator")
    @GenericGenerator(name = "id-generator", type = RecordIdGenerator.class )
    private String id;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

    private BigDecimal amount;
    private String note;
    private OffsetDateTime date;
    private OffsetDateTime exported;

    public String getId() {
        return Strings.isBlank(this.id) ? Util.calculateId(this) : this.id;
    }

    public void setId(String id) {
        this.id = this.id != null ? this.id : Util.calculateId(this);
    }

    /*
    private String category;
    private String currency;
    private BigDecimal ref_currency_amount;
    private String type;
    private String payment_type;
    private String payment_type_local;
    private double gps_latitude;
    private double gps_longitude;
    private double gps_accuracy_in_meters;
    private String warranty_in_month;
    private boolean transfer;
    private String payee;
    private String labels;
    private int envelope_id;
    private boolean custom_category;*/

}
