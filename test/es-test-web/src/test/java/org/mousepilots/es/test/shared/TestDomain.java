/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mousepilots.es.test.shared;

import com.google.gwt.thirdparty.guava.common.base.Objects;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.mousepilots.es.core.command.Command;
import org.mousepilots.es.core.model.AttributeES;
import org.mousepilots.es.core.model.AttributeVisitor;
import org.mousepilots.es.core.model.BasicTypeES;
import org.mousepilots.es.core.model.CollectionAttributeES;
import org.mousepilots.es.core.model.EmbeddableTypeES;
import org.mousepilots.es.core.model.EntityTypeES;
import org.mousepilots.es.core.model.ListAttributeES;
import org.mousepilots.es.core.model.ManagedTypeES;
import org.mousepilots.es.core.model.MapAttributeES;
import org.mousepilots.es.core.model.MappedSuperclassTypeES;
import org.mousepilots.es.core.model.MemberES;
import org.mousepilots.es.core.model.PluralAttributeES;
import org.mousepilots.es.core.model.SetAttributeES;
import org.mousepilots.es.core.model.SingularAttributeES;
import org.mousepilots.es.core.model.TypeVisitor;
import org.mousepilots.es.core.model.impl.EntityManagerImpl;
import org.mousepilots.es.core.model.impl.ManagedTypeESImpl;
import org.mousepilots.es.core.model.proxy.Proxy;
import org.mousepilots.es.test.domain.Gender;
import org.mousepilots.es.test.domain.embeddables.Address;
import org.mousepilots.es.test.domain.embeddables.Address_ES;
import org.mousepilots.es.test.domain.entities.Employee;
import org.mousepilots.es.test.domain.entities.Employee_ES;
import org.mousepilots.es.test.domain.entities.Manager;
import org.mousepilots.es.test.domain.entities.ManagerAccount;
import org.mousepilots.es.test.domain.entities.ManagerAccount_ES;
import org.mousepilots.es.test.domain.entities.Manager_ES;
import org.mousepilots.es.test.domain.entities.Role;
import org.mousepilots.es.test.domain.entities.Role_ES;
import org.mousepilots.es.test.domain.entities.WorkEnvironment;
import org.mousepilots.es.test.domain.entities.WorkEnvironment_ES;
import org.mousepilots.es.test.server.ScenarioServiceBean;
import org.mousepilots.es.test.server.domain.mmx.JPA;

/**
 *
 * @author AP34WV
 */
public class TestDomain extends AbstractTest {
    
    private static boolean isNullOrEmpty(Collection c){
        return c==null || c.isEmpty();
    }
    
    private static boolean isNullOrEmpty(Map m){
        return m==null || m.isEmpty();
    }
    

    private static class Pair {

        private final Object a, b;

        private Pair(Object a, Object b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 23 * hash + java.util.Objects.hashCode(this.a);
            hash = 23 * hash + java.util.Objects.hashCode(this.b);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Pair other = (Pair) obj;
            if (!java.util.Objects.equals(this.a, other.a)) {
                return false;
            }
            if (!java.util.Objects.equals(this.b, other.b)) {
                return false;
            }
            return true;
        }
        
        boolean isNullEqual(){
            return (a==null) == (b==null);
        }
        
        boolean isNull(){
            return a==null && b==null;
        }
    }

    private final ScenarioServiceBean scenarioServiceBean = new ScenarioServiceBean();

    public TestDomain() {
        super(TestDomain.class);
    }

    private final TypeVisitor<Boolean, Pair> typeValueComparator = new TypeVisitor<Boolean, Pair>() {

        private final Set<Pair> visited = new HashSet();

        @Override
        public Boolean visit(BasicTypeES t, Pair p) {
            return Objects.equal(p.a, p.b);
        }

        private boolean visitManagedType(ManagedTypeES t, Pair p) {
            
            if(!p.isNullEqual()){
                return false;
            }
            
            if(p.isNull()){
                return true;
            }
            
            if (visited.contains(p)) {
                return true;
            } else {
                visited.add(p);
                final Set<AttributeES> attributes = t.getAttributes();
                for (AttributeES a : attributes) {
                    final MemberES javaMember = a.getJavaMember();
                    final Pair attributeValuePair = new Pair(javaMember.get(p.a), javaMember.get(p.b));
                    final boolean same = (Boolean) a.accept(attributeValueComparator, attributeValuePair);
                    if (!same) {
                        return false;
                    }
                }
                return true;
            }

        }

        @Override
        public Boolean visit(EmbeddableTypeES t, Pair p) {
            return visitManagedType(t, p);
        }

        @Override
        public Boolean visit(MappedSuperclassTypeES t, Pair p) {
            return visitManagedType(t, p);
        }

        @Override
        public Boolean visit(EntityTypeES t, Pair p) {
            return visitManagedType(t, p);
        }
    };

    private final AttributeVisitor<Boolean, Pair> attributeValueComparator = new AttributeVisitor<Boolean, Pair>() {
        @Override
        public Boolean visit(SingularAttributeES a, Pair p) {
            if (a.isId() || a.isVersion()) {
                return true;
            } else {
                return (Boolean) a.getType().accept(typeValueComparator, p);
            }
        }
        
        private boolean visitJavaUtilCollection(PluralAttributeES a, Pair p){
            final Collection va = (Collection) p.a,vb= (Collection) p.b;
            if(isNullOrEmpty(va)==isNullOrEmpty(vb)){
                if(isNullOrEmpty(va) && isNullOrEmpty(vb)){
                    return true;
                } else {
                    return va.size()==vb.size();
                }
            } else {
                return false;
            }
        }

        @Override
        public Boolean visit(CollectionAttributeES a, Pair p) {
            return visitJavaUtilCollection(a, p);
        }

        @Override
        public Boolean visit(ListAttributeES a, Pair p) {
            return visitJavaUtilCollection(a, p);
        }

        @Override
        public Boolean visit(SetAttributeES a, Pair p) {
            return visitJavaUtilCollection(a, p);
        }

        @Override
        public Boolean visit(MapAttributeES a, Pair p) {
            Map va = (Map) p.a, vb = (Map) p.b;
            if(isNullOrEmpty(va)==isNullOrEmpty(vb)){
                if(isNullOrEmpty(va) && isNullOrEmpty(vb)){
                    return true;
                } else {
                    return va.size()==vb.size();
                }
            } else {
                return false;
            }
        }
    };

    public void testCreateDomain() {
        final EntityManagerImpl entityManagerES = (EntityManagerImpl) JPA.createEntityManagerES();
        final Role manager = entityManagerES.create(Role_ES.__TYPE);
        manager.setName("manager");
        final Role employee = entityManagerES.create(Role_ES.__TYPE);
        employee.setName("employee");

        final Employee john = entityManagerES.create(Employee_ES.__TYPE);
        john.setAge(21);
        john.setFirstName("John");
        john.setLastName("Doe");
        john.setUserName("john");
        john.setGender(Gender.MALE);
        employee.getUsers().add(john);
        john.getRoles().add(employee);

        final Manager bill = entityManagerES.create(Manager_ES.__TYPE);
        bill.getSubordinates().add(john);
        john.setManager(bill);
        bill.setUserName("bill");
        bill.setAge(40);
        bill.setGender(Gender.MALE);
        bill.getEmailAddresses().addAll(Arrays.asList("bill@gates.org", "elon@musk.org"));
        bill.setFirstName("Bill");
        bill.setLastName("Gates");
        bill.getRoles().addAll(Arrays.asList(manager, employee));
        manager.getUsers().add(bill);
        employee.getUsers().add(bill);

        WorkEnvironment hq = entityManagerES.create(WorkEnvironment_ES.__TYPE);
        hq.setOfficeLocation("Redmond");
        hq.setOfficeNumber(String.valueOf("1, top floor"));
        bill.setWorkEnvironment(hq);

        final ManagerAccount account = entityManagerES.create(ManagerAccount_ES.__TYPE);
        account.setDescription("Bill's account");
        bill.setAccount(account);

        for (int i = 0; i < 5; i++) {
            final Address a = entityManagerES.create(Address_ES.__TYPE);
            a.setCity("city " + i);
            a.setCountry("country " + i);
            a.setHouseNumber(String.valueOf(i));
            a.setStreet("street " + i);
            a.setZipCode("zipcode " + i);
            if (i < 4) {
                bill.getAddresses().add(a);
            } else {
                bill.setManagerAddress(a);
            }
        }
        final List<Command> commands = entityManagerES.getTransaction().getCommands();
        this.scenarioServiceBean.submit(commands);
        for (Command command : commands) {
            final Proxy proxy = command.getProxy();
            final Object subject = command.getRealSubject();
            final ManagedTypeESImpl type = proxy.__getProxyAspect().getType();
            Pair pair = new Pair(proxy,subject);
            final Boolean equal = (Boolean) type.accept(typeValueComparator, pair);
            assertTrue(proxy + " and " + subject + " are not equal w.r.t. their basic-typed singular attributes", equal);
        }

    }
}