import numpy as np

from bibliopixel import animation
from bibliopixel.colors import COLORS, hsv2rgb

"""
This is an example Animation class which has a single field -
`color` with the default value `COLORS.green`.

You can edit this as you like, add and remove fields and
write Python code in general.
"""

class Lights(animation.BaseAnimation):
    def __init__(
            self, *args,
            #################

            # length=10,

            #################
            **kwds):

        super().__init__(*args, **kwds)

        ###############################33

        self.length = 144 # length
        self.field = np.random.sample((self.length, 3))
        self.increment_factor = 0.0
        self.weight = np.array([0.0, 0.9, 0.1])
        self.neighbor_kernel = np.repeat(self.weight, 3).reshape((3, 3))
        self.field_increment = np.random.sample(
            (self.length, 3)) * self.increment_factor - (0.5 * self.increment_factor)
        self.flip_threshold = 0.05

    def step_chase(self, amt=1):
        this_pixel = self.cur_step % len(self.color_list)

        self.color_list[this_pixel] = COLORS.goldenrod
        self.color_list[this_pixel - 1] = COLORS.black

        self.color_list[this_pixel - 5] = COLORS.green
        self.color_list[this_pixel - 11] = COLORS.black

        self.color_list[this_pixel - 30] = COLORS.blue
        self.color_list[this_pixel - 41] = COLORS.red
        self.color_list[this_pixel - 55] = COLORS.black

    def step_thing(self, amt=1):
        if np.random.sample() < self.flip_threshold:
            flip_start = np.random.choice(self.length)
            flip_length = np.random.randint(13) + 5
            flip_range = np.array(range(flip_start, flip_start + flip_length))
            flip_range %= self.length

            self.field[flip_range] += 0.5

        self.field += self.field_increment
        self.field[np.where(self.field < 0)] += 1.0
        self.field[np.where(self.field > 1.0)] -= 1.0
        next_field = np.zeros((self.length, 3))

        for pixel in range(self.length):
            hsv = self.field[pixel]
            before = self.field[(pixel - 1) % self.length]
            after = self.field[(pixel + 1) % self.length]
            neighbors = np.array([before, hsv, after])
            become = np.sum(neighbors * self.neighbor_kernel, axis=0)
            next_field[pixel] = become
            
            rgb = hsv2rgb((
                int(become[0] * 255),
                int(become[1] * 255),
                int(become[2] * 255)))
            self.color_list[pixel] = rgb

        self.field = next_field

    def step(self, amt=1):
        # self.step_chase(amt)
        self.step_chase(amt)

    # pre_run is called right before the animation starts running.
    def pre_run(self):
        super().pre_run()
        # Your code goes here.

    def cleanup(self, clean_layout=True):
        super().cleanup(clean_layout)
        # Your code goes here.
