/*
 Copyright 2016  Richard Robinson @ NHS Digital <rrobinson@nhs.net>

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package org.medipi.concentrator.dao;

import java.util.List;
import org.medipi.concentrator.entities.Patient;
import org.springframework.stereotype.Repository;

/**
 * Implementation of Data Access Object for Patient
 * @author rick@robinsonhq.com
 */
@Repository
public class PatientDAOImpl extends GenericDAOImpl<Patient> implements PatientDAO {
    @Override
    public List<Patient> findByGroup(String patientGroupUuid) {
        return this.getEntityManager().createNamedQuery("Patient.findByGroup", Patient.class)
                .setParameter("patientGroupUuid", patientGroupUuid)
                .getResultList();
    }

    
}