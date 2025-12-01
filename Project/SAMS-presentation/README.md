# SAMS Presentation

Interactive presentation for the Student Academic Management System (SAMS) project, built with Vue.js 3 and Tailwind CSS.

## Tech Stack

- **Framework:** Vue.js 3.5
- **Styling:** Tailwind CSS 4
- **Animations:** GSAP
- **Icons:** Lucide Vue
- **Build Tool:** Vite

## Prerequisites

| Requirement | Version | Check Command |
|-------------|---------|---------------|
| Node.js | 18+ | `node -version` |
| npm | 9+ | `npm -version` |

---

## Running the Presentation

### Option 1: Using WSL (Windows Subsystem for Linux)

```bash
# Navigate to presentation folder
cd /mnt/c/Users/YOUR_USERNAME/path/to/SAMS-presentation

# Install dependencies (first time only)
npm install

# Start the presentation
npm run dev
```

### Option 2: Using Windows PowerShell

```powershell
# Navigate to presentation folder
cd "C:\Users\YOUR_USERNAME\path\to\SAMS-presentation"

# Install dependencies (first time only)
npm install

# Start the presentation
npm run dev
```

---

## Access the Presentation

After running `npm run dev`, open your browser and go to:

```
http://localhost:5173
```

---

## Navigation

- Use **arrow keys** or **click** to navigate between slides
- Press **F** for fullscreen mode (if supported)
- Press **Escape** to exit fullscreen

---

## Build for Production

To create a static build of the presentation:

```bash
# Build the presentation
npm run build

# Preview the build locally
npm run preview
```

The production files will be in the `dist/` folder.

---

## Project Structure

```
SAMS-presentation/
├── src/
│   ├── components/     # Slide components
│   ├── App.vue         # Main presentation container
│   └── main.js         # Entry point
├── public/             # Static assets
├── index.html          # HTML template
├── package.json        # Dependencies
├── tailwind.config.js  # Tailwind configuration
├── vite.config.js      # Vite configuration
└── README.md
```

---

## Troubleshooting

### Port Already in Use

```bash
# WSL/Linux
lsof -i :5173
kill -9 <PID>

# PowerShell
netstat -ano | findstr :5173
taskkill /PID <PID> /F
```

### Node Modules Issues

```bash
rm -rf node_modules package-lock.json
npm install
```

---

## Related

- [SAMS Code](../Project%20Implementation/SAMS-code/) - Main application source code
