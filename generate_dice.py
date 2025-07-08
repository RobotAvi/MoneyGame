import numpy as np
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d.art3d import Poly3DCollection
from PIL import Image, ImageDraw

# 1. Генерация граней с точками (2D PNG)
def draw_face(dots, filename, size=80, dot_radius=8):
    img = Image.new('RGBA', (size, size), (255, 255, 255, 0))
    draw = ImageDraw.Draw(img)
    # Белый квадрат с прозрачным фоном
    draw.rounded_rectangle([(0, 0), (size-1, size-1)], radius=12, fill=(255,255,255,255))
    # Центры точек (относительно центра)
    positions = {
        1: [(0, 0)],
        2: [(-0.25, -0.25), (0.25, 0.25)],
        3: [(-0.3, -0.3), (0, 0), (0.3, 0.3)],
        4: [(-0.25, -0.25), (-0.25, 0.25), (0.25, -0.25), (0.25, 0.25)],
        5: [(-0.25, -0.25), (-0.25, 0.25), (0.25, -0.25), (0.25, 0.25), (0, 0)],
        6: [(-0.3, -0.25), (-0.3, 0), (-0.3, 0.25), (0.3, -0.25), (0.3, 0), (0.3, 0.25)],
    }
    for x, y in positions[dots]:
        cx = size // 2 + int(x * size)
        cy = size // 2 + int(y * size)
        draw.ellipse([(cx-dot_radius, cy-dot_radius), (cx+dot_radius, cy+dot_radius)], fill=(0,0,0,255))
    img.save(filename)

for i in range(1, 7):
    draw_face(i, f'face_{i}.png')

# 2. Генерация изометрических изображений кубика (Pillow)
# Классическая раскладка: напротив 1 — 6, 2 — 5, 3 — 4
# Для каждого dice_N.png фронтальная грань — N, верхняя и боковая — по классике
# Пары: (front, top, right)
dice_faces = {
    1: (1, 2, 3),
    2: (2, 6, 3),
    3: (3, 2, 1),
    4: (4, 2, 6),
    5: (5, 2, 4),
    6: (6, 2, 5),
}

# Изометрические координаты для граней (front, top, right)
def paste_face_iso(base, face_img, quad):
    # quad: 4 точки (x, y) в порядке [левый верх, правый верх, правый низ, левый низ]
    w, h = face_img.size
    # Преобразуем face_img в нужную форму
    coeffs = find_coeffs(
        [(0,0), (w,0), (w,h), (0,h)],
        quad
    )
    warped = face_img.transform(base.size, Image.PERSPECTIVE, coeffs, Image.BICUBIC)
    base.alpha_composite(warped)

def find_coeffs(pa, pb):
    # pa, pb — 4 точки (x, y)
    matrix = []
    for p1, p2 in zip(pa, pb):
        matrix.append([p1[0], p1[1], 1, 0, 0, 0, -p2[0]*p1[0], -p2[0]*p1[1]])
        matrix.append([0, 0, 0, p1[0], p1[1], 1, -p2[1]*p1[0], -p2[1]*p1[1]])
    A = np.matrix(matrix, dtype=np.float64)
    B = np.array(pb).reshape(8)
    res = np.dot(np.linalg.pinv(A), B)
    return list(res.A1)

# Координаты для изометрии (в пикселях)
size = 80
cx, cy = size//2, size//2
# front
front_quad = [
    (cx-24, cy+10), (cx+24, cy+10), (cx+24, cy+38), (cx-24, cy+38)
]
# top
top_quad = [
    (cx-24, cy+10), (cx+24, cy+10), (cx+12, cy-12), (cx-12, cy-12)
]
# right
right_quad = [
    (cx+24, cy+10), (cx+24, cy+38), (cx+44, cy+26), (cx+44, cy)
]

for n in range(1, 7):
    base = Image.new('RGBA', (size, size), (0,0,0,0))
    front, top, right = dice_faces[n]
    paste_face_iso(base, Image.open(f'face_{front}.png'), front_quad)
    paste_face_iso(base, Image.open(f'face_{top}.png'), top_quad)
    paste_face_iso(base, Image.open(f'face_{right}.png'), right_quad)
    base.save(f'dice_{n}.png')

# 2. Сборка 3D-куба с текстурами граней и рендеринг
face_imgs = [plt.imread(f'face_{i}.png') for i in range(1, 7)]

# Вершины куба
verts = np.array([
    [-0.5, -0.5, -0.5],  # 0
    [ 0.5, -0.5, -0.5],  # 1
    [ 0.5,  0.5, -0.5],  # 2
    [-0.5,  0.5, -0.5],  # 3
    [-0.5, -0.5,  0.5],  # 4
    [ 0.5, -0.5,  0.5],  # 5
    [ 0.5,  0.5,  0.5],  # 6
    [-0.5,  0.5,  0.5],  # 7
])

# Грани куба (индексы вершин)
faces = [
    [0, 1, 2, 3],  # 0: front (1)
    [4, 5, 6, 7],  # 1: back  (2)
    [0, 1, 5, 4],  # 2: bottom(3)
    [2, 3, 7, 6],  # 3: top   (4)
    [1, 2, 6, 5],  # 4: right (5)
    [0, 3, 7, 4],  # 5: left  (6)
]

# Соответствие: номер файла -> индекс грани
face_order = [0, 1, 2, 3, 4, 5]  # 1-6

# Повороты для каждой грани к зрителю (elev, azim)
view_angles = [
    (30, 45),   # front (1)
    (30, 225),  # back  (2)
    (120, 45),  # bottom(3)
    (-60, 45),  # top   (4)
    (30, 135),  # right (5)
    (30, -45),  # left  (6)
]

def plot_cube_with_textures(face_imgs, filename, elev, azim):
    fig = plt.figure(figsize=(1, 1), dpi=80)
    ax = fig.add_subplot(111, projection='3d')
    ax.set_facecolor((0,0,0,0))
    fig.patch.set_alpha(0.0)
    # Для каждой грани накладываем текстуру
    for i, face in enumerate(faces):
        x = verts[face, 0]
        y = verts[face, 1]
        z = verts[face, 2]
        img = face_imgs[i]
        ax.plot_surface(
            np.array([x, x]),
            np.array([y, y]),
            np.array([z, z]),
            rstride=1, cstride=1, facecolors=img, shade=False)
    ax.view_init(elev=elev, azim=azim)
    ax.set_xlim(-0.6, 0.6)
    ax.set_ylim(-0.6, 0.6)
    ax.set_zlim(-0.6, 0.6)
    ax.axis('off')
    plt.subplots_adjust(left=0, right=1, top=1, bottom=0)
    plt.savefig(filename, transparent=True, bbox_inches='tight', pad_inches=0)
    plt.close(fig)
    # Обрезаем до 80x80 и сохраняем прозрачность
    img = Image.open(filename).convert("RGBA")
    img = img.resize((80, 80), Image.LANCZOS)
    img.save(filename)

for i, (elev, azim) in enumerate(view_angles):
    plot_cube_with_textures(face_imgs, f'dice_{i+1}.png', elev, azim) 