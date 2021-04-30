package beans.refactoring;

import beans.other.RefactoringForDatabase;
import gr.uom.java.xmi.diff.CodeRange;
import org.eclipse.jgit.lib.ObjectId;
import org.refactoringminer.api.Refactoring;

import modules.git.GitController;
import utils.setting.SettingManager;
import beans.source.MethodDefinition;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
/**
 * This class is a wrapper of Refactoring class provided by Refactoring Miner
 * This class is used to store/get data from Database (hibernate)
 */
@Entity
@Table(name="refactoring", schema = "result" )
public class Refactoring2 implements Cloneable, Serializable {
    @Transient
    private static final long serialVersionUID = 1L;
    /**
     * this ID is automatically generated for each table by hibernate
     */
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name="id", columnDefinition="biginit")
    public long id;
    /**
     * how many methods call from the test exercise to the refactoring
     */
    @Column(name = "layer")
    public Integer layer;
    /**
     * the origin of the refactoring
     */
    @Column(name = "whomade",  columnDefinition="TEXT")
    public String whoMade;
    /**
     * hash id of refactoring provided by refactoring miner
     */
    @Column(name = "refactoringhash")
    public Integer refactoringHash;
    /**
     * refactoring type
     * e.g., Move Class, Extract Method
     */
    @Column(name = "type",  columnDefinition="TEXT")
    public String type;
    @Transient
    public Refactoring refactoring;

    public Refactoring2(MethodDefinition md, Refactoring rf){
        this.layer = 0;
        this.whoMade = md.getSignature();
        this.refactoring = rf;
        this.refactoringHash = rf.hashCode();
        this.type = rf.getRefactoringType().getDisplayName();
    }
    public Refactoring2(){}
    public Refactoring2 clone() {
        Refactoring2 cloned = null;
        try {
            cloned = (Refactoring2) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new AssertionError();
        }
        return cloned;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + refactoring.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object anObject) {
        if(anObject instanceof Refactoring2){
            Refactoring2 r = (Refactoring2)anObject;
            return refactoring.equals(r.refactoring);
        }
        return false;
    }
}
