const express = require('express');
const fs = require('fs');
const path = require('path');

const app = express();
const PORT = 3001;
const DATA_FILE = path.join(__dirname, 'grades.json');

// Middleware
app.use(express.json());

// Enable CORS for all origins (needed for Java client)
app.use((req, res, next) => {
    res.header('Access-Control-Allow-Origin', '*');
    res.header('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
    res.header('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept, Authorization');
    if (req.method === 'OPTIONS') {
        return res.sendStatus(200);
    }
    next();
});

// Logging middleware
app.use((req, res, next) => {
    console.log(`[${new Date().toISOString()}] ${req.method} ${req.path}`);
    console.log('  Headers:', JSON.stringify(req.headers, null, 2));
    console.log('  Body:', JSON.stringify(req.body, null, 2));
    next();
});

// Load grades from file
function loadGrades() {
    try {
        if (fs.existsSync(DATA_FILE)) {
            const data = fs.readFileSync(DATA_FILE, 'utf8');
            return JSON.parse(data);
        }
    } catch (error) {
        console.error('Error loading grades:', error);
    }
    return [];
}

// Save grades to file
function saveGrades(grades) {
    try {
        fs.writeFileSync(DATA_FILE, JSON.stringify(grades, null, 2));
        return true;
    } catch (error) {
        console.error('Error saving grades:', error);
        return false;
    }
}

// GET all grades
app.get('/api/v1/grades', (req, res) => {
    const grades = loadGrades();
    res.json({
        success: true,
        count: grades.length,
        data: grades
    });
});

// GET single grade by ID
app.get('/api/v1/grades/:id', (req, res) => {
    const grades = loadGrades();
    const grade = grades.find(g => g.scoreId === parseInt(req.params.id));
    
    if (!grade) {
        return res.status(404).json({
            success: false,
            message: 'Grade not found'
        });
    }
    
    res.json({
        success: true,
        data: grade
    });
});

// POST create new grade
app.post('/api/v1/grades', (req, res) => {
    const grades = loadGrades();
    const newGrade = req.body;
    
    // Check if grade with this ID already exists
    const existingIndex = grades.findIndex(g => g.scoreId === newGrade.scoreId);
    if (existingIndex !== -1) {
        return res.status(409).json({
            success: false,
            message: `Grade with ID ${newGrade.scoreId} already exists`
        });
    }
    
    // Add timestamp
    newGrade.syncedAt = new Date().toISOString();
    
    grades.push(newGrade);
    
    if (saveGrades(grades)) {
        console.log(`✅ Grade created: ${newGrade.studentId} - ${newGrade.note}/${newGrade.noteSur}`);
        res.status(201).json({
            success: true,
            message: 'Grade created successfully',
            data: newGrade
        });
    } else {
        res.status(500).json({
            success: false,
            message: 'Failed to save grade'
        });
    }
});

// PUT update grade
app.put('/api/v1/grades/:id', (req, res) => {
    const grades = loadGrades();
    const id = parseInt(req.params.id);
    const updatedGrade = req.body;
    
    const index = grades.findIndex(g => g.scoreId === id);
    if (index === -1) {
        return res.status(404).json({
            success: false,
            message: 'Grade not found'
        });
    }
    
    // Update the grade
    updatedGrade.scoreId = id; // Ensure ID consistency
    updatedGrade.updatedAt = new Date().toISOString();
    updatedGrade.syncedAt = new Date().toISOString();
    
    grades[index] = updatedGrade;
    
    if (saveGrades(grades)) {
        console.log(`✏️ Grade updated: ${updatedGrade.studentId} - ${updatedGrade.note}/${updatedGrade.noteSur}`);
        res.json({
            success: true,
            message: 'Grade updated successfully',
            data: updatedGrade
        });
    } else {
        res.status(500).json({
            success: false,
            message: 'Failed to update grade'
        });
    }
});

// DELETE grade
app.delete('/api/v1/grades/:id', (req, res) => {
    const grades = loadGrades();
    const id = parseInt(req.params.id);
    
    const index = grades.findIndex(g => g.scoreId === id);
    if (index === -1) {
        return res.status(404).json({
            success: false,
            message: 'Grade not found'
        });
    }
    
    const deletedGrade = grades[index];
    grades.splice(index, 1);
    
    if (saveGrades(grades)) {
        console.log(`🗑️ Grade deleted: ID ${id}`);
        res.json({
            success: true,
            message: 'Grade deleted successfully',
            data: deletedGrade
        });
    } else {
        res.status(500).json({
            success: false,
            message: 'Failed to delete grade'
        });
    }
});

// Health check endpoint
app.get('/health', (req, res) => {
    res.json({ status: 'OK', timestamp: new Date().toISOString() });
});

// Start server
app.listen(PORT, () => {
    console.log('='.repeat(50));
    console.log('🚀 Grade API Server Started!');
    console.log('='.repeat(50));
    console.log(`📍 URL: http://localhost:${PORT}`);
    console.log(`📊 Endpoints:`);
    console.log(`   GET    http://localhost:${PORT}/api/v1/grades`);
    console.log(`   POST   http://localhost:${PORT}/api/v1/grades`);
    console.log(`   PUT    http://localhost:${PORT}/api/v1/grades/:id`);
    console.log(`   DELETE http://localhost:${PORT}/api/v1/grades/:id`);
    console.log(`🏥 Health: http://localhost:${PORT}/health`);
    console.log('='.repeat(50));
    console.log(`💾 Data file: ${DATA_FILE}`);
    console.log('');
    console.log('To test the API, run:');
    console.log(`  curl http://localhost:${PORT}/health`);
    console.log('='.repeat(50));
});
