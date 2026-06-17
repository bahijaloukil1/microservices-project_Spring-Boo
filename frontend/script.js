const GATEWAY_URL = 'http://localhost:3000';

// ========== 1. Gestion des étudiants ==========

// Ajouter un étudiant
async function addStudent() {
    const student = {
        cnie: document.getElementById('cnie').value,
        firstName: document.getElementById('firstName').value,
        lastName: document.getElementById('lastName').value,
        email: document.getElementById('email').value
    };
    
    try {
        const response = await fetch(`${GATEWAY_URL}/api/students`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(student)
        });
        const data = await response.json();
        document.getElementById('addResult').innerHTML = '<p class="success">✅ Étudiant ajouté avec succès! ID: ' + data.id + '</p>';
        loadStudents();
        // Vider les champs
        document.getElementById('cnie').value = '';
        document.getElementById('firstName').value = '';
        document.getElementById('lastName').value = '';
        document.getElementById('email').value = '';
    } catch (error) {
        document.getElementById('addResult').innerHTML = '<p class="error">❌ Erreur: ' + error.message + '</p>';
    }
}

// Charger tous les étudiants
async function loadStudents() {
    try {
        const response = await fetch(`${GATEWAY_URL}/api/students`);
        const students = await response.json();
        const list = document.getElementById('studentsList');
        if (students.length === 0) {
            list.innerHTML = '<li>Aucun étudiant trouvé</li>';
        } else {
            list.innerHTML = students.map(s => 
                `<li><strong>ID:</strong> ${s.id} | <strong>CNIE:</strong> ${s.cnie} | <strong>Nom:</strong> ${s.firstName} ${s.lastName} | <strong>Email:</strong> ${s.email}</li>`
            ).join('');
        }
    } catch (error) {
        document.getElementById('studentsList').innerHTML = '<li class="error">❌ Erreur de connexion</li>';
    }
}

// ========== 2. Gestion des cours ==========

// Charger tous les cours
async function loadCourses() {
    try {
        const response = await fetch(`${GATEWAY_URL}/api/courses`);
        const courses = await response.json();
        const list = document.getElementById('coursesList');
        if (courses.length === 0) {
            list.innerHTML = '<li>Aucun cours trouvé</li>';
        } else {
            list.innerHTML = courses.map(c => 
                `<li><strong>ID:</strong> ${c.id} | <strong>Titre:</strong> ${c.title} | <strong>Description:</strong> ${c.description} | <strong>Crédits:</strong> ${c.credits}</li>`
            ).join('');
        }
    } catch (error) {
        document.getElementById('coursesList').innerHTML = '<li class="error">❌ Erreur de connexion</li>';
    }
}

// ========== 3. Gestion des inscriptions ==========

// Inscrire un étudiant à un cours
async function enrollStudent() {
    const enrollment = {
        studentId: parseInt(document.getElementById('studentId').value),
        courseId: parseInt(document.getElementById('courseId').value)
    };
    
    try {
        const response = await fetch(`${GATEWAY_URL}/api/enrollments`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(enrollment)
        });
        const result = await response.text();
        if (response.ok) {
            document.getElementById('enrollResult').innerHTML = '<p class="success">✅ ' + result + '</p>';
            document.getElementById('studentId').value = '';
            document.getElementById('courseId').value = '';
        } else {
            document.getElementById('enrollResult').innerHTML = '<p class="error">❌ ' + result + '</p>';
        }
    } catch (error) {
        document.getElementById('enrollResult').innerHTML = '<p class="error">❌ Erreur: ' + error.message + '</p>';
    }
}

// Voir les inscriptions d'un étudiant (Dashboard)
async function getEnrollments() {
    const studentId = document.getElementById('viewStudentId').value;
    
    if (!studentId) {
        document.getElementById('enrollmentsList').innerHTML = '<li class="error">❌ Veuillez entrer un ID étudiant</li>';
        return;
    }
    
    try {
        const response = await fetch(`${GATEWAY_URL}/api/enrollments/student/${studentId}`);
        const enrollments = await response.json();
        const list = document.getElementById('enrollmentsList');
        
        if (enrollments.length === 0) {
            list.innerHTML = '<li>Aucune inscription trouvée pour cet étudiant</li>';
        } else {
            list.innerHTML = enrollments.map(e => 
                `<li><strong>📌 Inscription #${e.enrollmentId}</strong><br>
                 👨‍🎓 CNIE: ${e.studentCnie}<br>
                 📚 Cours: ${e.courseName}<br>
                 📅 Date: ${e.date}<br>
                 🕐 Supprimable: ${e.deletable ? '✅ Oui (moins de 24h)' : '❌ Non (plus de 24h)'}
                 ${e.deletable ? `<br><button onclick="deleteEnrollment(${e.enrollmentId})">🗑️ Supprimer</button>` : ''}
                 </li>`
            ).join('');
        }
    } catch (error) {
        list.innerHTML = '<li class="error">❌ Erreur de connexion</li>';
    }
}

// Supprimer une inscription
async function deleteEnrollment(enrollmentId) {
    if (!confirm('Êtes-vous sûr de vouloir supprimer cette inscription ?')) {
        return;
    }
    
    try {
        const response = await fetch(`${GATEWAY_URL}/api/enrollments/${enrollmentId}`, {
            method: 'DELETE'
        });
        const result = await response.text();
        alert(result);
        // Rafraîchir la liste des inscriptions
        const studentId = document.getElementById('viewStudentId').value;
        getEnrollments();
    } catch (error) {
        alert('Erreur: ' + error.message);
    }
}

// ========== 4. Chargement initial ==========
loadStudents();
loadCourses();